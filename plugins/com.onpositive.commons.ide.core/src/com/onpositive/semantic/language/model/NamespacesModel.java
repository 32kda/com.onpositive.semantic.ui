package com.onpositive.semantic.language.model;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.pde.internal.core.natures.PDE;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.onpositive.semantic.model.realm.HashDelta;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.Realm;

public class NamespacesModel {

	private static final String EXTENSION = "extension"; //$NON-NLS-1$
	private static final String POINT = "point"; //$NON-NLS-1$
	private static final String COM_onpositive_COMMONS_PLATFORM_NAMESPACES = "com.onpositive.commons.platform.namespaces"; //$NON-NLS-1$

	private final HashMap<IProject, ProjectContibutionModel> map = new HashMap<IProject, ProjectContibutionModel>();

	private final HashMap<String, NamespaceModel> namespaceMap = new HashMap<String, NamespaceModel>();

	private Realm<NamespaceModel> models = new Realm<NamespaceModel>();

	public IRealm<NamespaceModel> getModels() {
		return this.models;
	}

	private NamespacesModel() {
		this.register(null, this.parsePlaform());
		for (final IProject p : ResourcesPlugin.getWorkspace().getRoot()
				.getProjects()) {
			final ArrayList<NameSpaceContributionModel> parseProjectXMLFile = this
					.parseProjectXMLFile(p);
			if (parseProjectXMLFile != null) {
				this.register(p, parseProjectXMLFile);
			}
		}
		this.models = new Realm<NamespaceModel>(this.namespaceMap.values());
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				new IResourceChangeListener() {

					@SuppressWarnings("unchecked")
					public void resourceChanged(IResourceChangeEvent event) {
						final HashMap<String, NamespaceModel> ma = new HashMap<String, NamespaceModel>(
								NamespacesModel.this.namespaceMap);
						NamespacesModel.this.processDelta(event.getResource(),
								event.getDelta());
						final HashDelta<NamespaceModel> buildFrom = HashDelta
								.buildFrom(ma.values(),
										NamespacesModel.this.namespaceMap
												.values());
						for (final NamespaceModel m : ma.values()) {
							if (!buildFrom.getAddedElements().contains(m)
									&& !buildFrom.getRemovedElements()
											.contains(m)) {
								final NamespaceModel namespaceModel = NamespacesModel.this.namespaceMap
										.get(m.getUrl());
								if (!m.actuallyEquals(namespaceModel)) {
									buildFrom.markChanged(m);
								}
							}
						}
						NamespacesModel.this.models.applyDelta(buildFrom);
					}
				});
	}

	@SuppressWarnings("restriction")
	protected void processDelta(IResource resource, IResourceDelta delta) {
		if (resource != null) {
			if (resource.getName().equals("plugin.xml") || resource.getName().endsWith(".dlm")) { //$NON-NLS-1$
				if (PDE.hasPluginNature(resource.getProject())) {
					this.reparse(resource.getProject());
				}
			}
		}
		if (delta != null) {
			final IResourceDelta[] affectedChildren = delta
					.getAffectedChildren();
			for (final IResourceDelta d : affectedChildren) {
				this.processDelta(d.getResource(), d);
			}
		}
	}

	private void reparse(IProject project) {
		ProjectContibutionModel projectContibutionModel = this.map
				.get(project);
		if (projectContibutionModel==null){
			register(project, new ArrayList<NameSpaceContributionModel>());
			projectContibutionModel=this.map.get(project);
		}
		final HashSet<NamespaceModel> ms = new HashSet<NamespaceModel>();
		for (final NameSpaceContributionModel m : projectContibutionModel
				.getNamespaces()) {
			final NamespaceModel namespaceModel = this.namespaceMap.get(m
					.getUrl());
			if (namespaceModel != null) {
				namespaceModel.unregister(project, namespaceModel);

				if (namespaceModel.isEmpty()) {
					ms.add(namespaceModel);
				}
			}
		}
		for (final NamespaceModel m : ms) {
			this.namespaceMap.remove(m.getUrl());
		}
		this.map.remove(project);

		final ArrayList<NameSpaceContributionModel> parseProjectXMLFile = this
				.parseProjectXMLFile(project);
		if (parseProjectXMLFile != null) {
			this.map.put(project, new ProjectContibutionModel(
					parseProjectXMLFile, project));
			for (final NameSpaceContributionModel m : parseProjectXMLFile) {
				final String url = m.getUrl();
				final NamespaceModel namespaceModel = new NamespaceModel(url);
				this.namespaceMap.put(url, namespaceModel);
				namespaceModel.register(project, m);
			}
		}
		HashSet<Runnable> hashSet = listeners.get(project);
		if (hashSet!=null){
			for (Runnable r:hashSet){
				r.run();
			}
		}
	}

	private void register(IProject project,
			ArrayList<NameSpaceContributionModel> parseProjectXMLFile) {
		this.map.put(project, new ProjectContibutionModel(parseProjectXMLFile,
				project));
		for (final NameSpaceContributionModel ma : parseProjectXMLFile) {
			NamespaceModel namespaceModel = this.namespaceMap.get(ma.getUrl());
			if (namespaceModel == null) {
				namespaceModel = new NamespaceModel(ma.getUrl());
				this.namespaceMap.put(ma.getUrl(), namespaceModel);
			}
			namespaceModel.register(project, ma);

		}
	}

	static NamespacesModel instance = new NamespacesModel();

	public static NamespacesModel getInstance() {
		return instance;
	}
	
	
	public ProjectContibutionModel getProjectModel(IProject prj){
		return map.get(prj);
	}

	private ArrayList<NameSpaceContributionModel> parsePlaform() {
		final IConfigurationElement[] configurationElementsFor = Platform
				.getExtensionRegistry().getConfigurationElementsFor(
						COM_onpositive_COMMONS_PLATFORM_NAMESPACES);
		final ArrayList<NameSpaceContributionModel> ns = new ArrayList<NameSpaceContributionModel>();
		for (final IConfigurationElement e : configurationElementsFor) {
			final String attribute = e.getAttribute("partDefinitionFile");
			String attribute2 = e.getAttribute("url");
			if (attribute != null) {
				final String name = e.getContributor().getName();
				final Bundle bundle = Platform.getBundle(name);
				final URL resource = bundle.getResource(attribute);
				IResourceLoader resourceLoader;
				URL resource2 = bundle.getResource(attribute+"-doc");
				if (resource2!=null){
					resourceLoader=new IResourceLoader(){

						public URL getResource(String name) {
							return bundle.getResource(name);
						}
						
					};
					
					this.parseUrl(ns, resource,resource2,name,resourceLoader,attribute2,null);
				}
				else{
					final Bundle bundle2 = Platform.getBundle(name+".ide");
					if (bundle2!=null){
					int pos=attribute.lastIndexOf('/');
						String sm=attribute;
							if (pos!=-1){
								sm=sm.substring(pos+1);
							}
						
						sm=sm+"-doc";
						resource2 = bundle2.getResource(sm);
					}
					resourceLoader=new IResourceLoader(){

						public URL getResource(String name) {
							return bundle2.getResource(name);
						}
						
					};
					this.parseUrl(ns, resource,resource2,name,resourceLoader,attribute2,null);
				}
			}
		}
		return ns;
	}

	private void parseUrl(ArrayList<NameSpaceContributionModel> result,
			URL resource, URL docFile, String name, IResourceLoader resourceLoader, String declUrl, IResource findMember) {
		if (resource != null) {
			try {
				final DocumentBuilderFactory newInstance = DocumentBuilderFactory
						.newInstance();
				final InputStream openStream = resource.openStream();
				DocumentBuilder newDocumentBuilder = newInstance
						.newDocumentBuilder();
				Document parse = newDocumentBuilder.parse(openStream);
				final NameSpaceContributionModel na = new NameSpaceContributionModel(
						parse.getDocumentElement());
				na.setResource((IFile) findMember);
				na.setUrlExt(declUrl);
				result.add(na);
				openStream.close();
				try {
					if (docFile != null) {
						InputStream contents = docFile.openStream();// this.
																	// docFile.
																	// getContents
																	// (true);
						try {
							parse = newDocumentBuilder.parse(contents);
							DocumentationContributionModel ma = new DocumentationContributionModel(
									na, parse);
							ma.setLoader(resourceLoader);
							na.setDocumentation(ma);
						} finally {
							contents.close();
						}
					} else {
						final DocumentationContributionModel ma = new DocumentationContributionModel(
								na);
						na.setDocumentation(ma);
					}
				} catch (final Exception e) {
					final DocumentationContributionModel ma = new DocumentationContributionModel(
							na);
					na.setDocumentation(ma);
				}
				// na.getDocumentation().setLocation(docFile.toExternalForm());
			} catch (final Exception e) {
				com.onpositive.core.runtime.Platform.log(e);
			}
		}
	}

	@SuppressWarnings("restriction")
	private ArrayList<NameSpaceContributionModel> parseProjectXMLFile(final IProject p) {
		final boolean hasPluginNature = PDE.hasPluginNature(p);
		if (hasPluginNature) {
			final IFile file = p.getFile("plugin.xml"); //$NON-NLS-1$
			final ArrayList<NameSpaceContributionModel> result = new ArrayList<NameSpaceContributionModel>();
			if (file.exists()) {
				try {
					final DocumentBuilderFactory newInstance = DocumentBuilderFactory
							.newInstance();
					final Document parse = newInstance.newDocumentBuilder()
							.parse(file.getContents());
					final Element documentElement = parse.getDocumentElement();
					final NodeList childNodes = documentElement.getChildNodes();
					for (int a = 0; a < childNodes.getLength(); a++) {
						final Node item = childNodes.item(a);
						if (item instanceof Element) {
							final Element el = (Element) item;
							if (el.getNodeName().equals(EXTENSION)) {
								if (el
										.getAttribute(POINT)
										.equals(
												COM_onpositive_COMMONS_PLATFORM_NAMESPACES)) {

									final NodeList childNodes2 = el
											.getChildNodes();
									for (int b = 0; b < childNodes2.getLength(); b++) {
										final Node item2 = childNodes2.item(b);
										if (item2 instanceof Element) {
											final Element ela = (Element) item2;
											final String attribute = ela
													.getAttribute("partDefinitionFile");
											String declUrl = ela.getAttribute("url");
											if ((attribute != null)
													&& (attribute.length() > 0)) {
												final IResource findMember = p
														.findMember(new Path(
																attribute));
												if (findMember != null) {
													final URL url = findMember
															.getRawLocation()
															.toFile().toURI()
															.toURL();
													IFile file2 = p.getProject().getFile(new Path(attribute+"-doc"));
													URL resource2 = file2.isAccessible()?file2.getLocationURI().toURL():null;
													IResourceLoader iResourceLoader = new IResourceLoader() {
														
														public URL getResource(String name) {
															try {
																return p.getProject().getFile(new Path(name)).getRawLocationURI().toURL();
															} catch (MalformedURLException e) {
																return null;
															}
														}
													};
													if (resource2==null){
														String pName=file2.getProject().getName()+".ide";
														final IProject project = file2.getProject().getWorkspace().getRoot().getProject(pName);
														int p0=attribute.lastIndexOf('/');
														String k=attribute+"-doc";
														if (p0!=-1){
															k=k.substring(p0+1);
															
														}
														resource2=project.getFile(k).getLocationURI().toURL();
														iResourceLoader = new IResourceLoader() {
															
															public URL getResource(String name) {
																try {
																	return project.getFile(new Path(name)).getRawLocationURI().toURL();
																} catch (MalformedURLException e) {
																	return null;
																}
															}
														};
													}
													
													this.parseUrl(result, url,resource2,
															p.getName(),iResourceLoader,declUrl,findMember);
												}
											}

										}
									}
								}
							}
						}
					}
				} catch (final Exception e) {
					com.onpositive.core.runtime.Platform.log(e);
				}
			}
			return result;
		}
		return null;
	}

	public NamespaceModel resolveNamespace(String namespace) {
		final NamespaceModel namespaceModel = this.namespaceMap.get(namespace);
		return namespaceModel;
	}

	public ElementModel resolveElement(String namespace, String tag) {
		final NamespaceModel namespaceModel = this.namespaceMap.get(namespace);
		if (namespaceModel != null) {
			return namespaceModel.resolveElement(tag);
		}
		if (namespace.charAt(namespace.length()-1)=='/'){
			return resolveElement(namespace.substring(0,namespace.length()-1), tag);
		}
		return null;
	}

	public AttributeModel resolveAttribute(String namespace, String localName) {
		final NamespaceModel namespaceModel = this.namespaceMap.get(namespace);
		if (namespaceModel != null) {
			return namespaceModel.resolveAttribute(localName);
		}
		return null;
	}

	public ArrayList<ModelElement> getAllElements() {
		final ArrayList<ModelElement> elements = new ArrayList<ModelElement>();
		for (final NamespaceModel m : this.namespaceMap.values()) {
			elements.addAll(m.getMembers());
		}
		return elements;
	}
	
	HashMap<IProject,HashSet<Runnable>>listeners=new HashMap<IProject, HashSet<Runnable>>();

	public void addModelListener(IProject project,Runnable r) {
		HashSet<Runnable> hashSet = listeners.get(project);
		if (hashSet==null){
			hashSet=new HashSet<Runnable>();
			listeners.put(project, hashSet);
		}
		hashSet.add(r);
	}
	
	public void removeModelListener(IProject project,Runnable r) {
		HashSet<Runnable> hashSet = listeners.get(project);
		if (hashSet!=null){
			hashSet.remove(r);
		}
	}
}