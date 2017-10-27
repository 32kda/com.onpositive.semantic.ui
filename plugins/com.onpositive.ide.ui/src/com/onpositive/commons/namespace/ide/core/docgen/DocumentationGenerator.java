package com.onpositive.commons.namespace.ide.core.docgen;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import com.onpositive.commons.Activator;
import com.onpositive.commons.namespace.ide.ui.core.GroupRegistry;
import com.onpositive.commons.platform.registry.GenericRegistryObject;
import com.onpositive.semantic.language.model.AttributeModel;
import com.onpositive.semantic.language.model.DocumentationContribution;
import com.onpositive.semantic.language.model.ElementModel;
import com.onpositive.semantic.language.model.NameSpaceContributionModel;

public class DocumentationGenerator {

	public static void generate(IContainer container,
			NameSpaceContributionModel object) {
		System.out.println(container);
		writeToFile(container.getFile(new Path("new")), "");
		writeToFile(container.getFile(new Path("index.html")),
				getSummary(object));
		for (final String s : object.getElementNames()) {
			writeToFile(container.getFile(new Path(s + ".html")),
					getInformation(object.getElement(s)));
		}
	}

	private static String getInformation(ElementModel element) {
		final StringBuilder bld = new StringBuilder();
		bld.append("<h4>");
		final NameSpaceContributionModel model = element.getModel();
		bld.append("Namespace:" + model.getName() + " (<a href=\"index.html\">"
				+ model.getUrl() + ")</a>");
		bld.append("</h4>");
		appendTag(bld, "h2", (element.isAbstract() ? "Abstract Element: "
				: "Element: ")
				+ element.getName());
		final ArrayList<AttributeModel> properties = new ArrayList<AttributeModel>(
				element.getAllProperties());
		bld.append("<p>");
		final String[] superElement = element.getSuperElement();
		bld.append("<b>Super elements: </b>");
		if ((superElement != null) && (superElement.length != 0)) {
			for (final String s : superElement) {
				bld.append("<a href=\"./" + s + ".html\" >");
				bld.append(s);
				bld.append("</a>");
				bld.append(" ");
			}
		} else {
			bld.append(" None");
		}
		final String[] childs = element.getChilds();
		final Collection<ElementModel> subElements = element.getModel()
				.getSubElements(element);
		final ArrayList<String> str = new ArrayList<String>();
		for (final ElementModel m : subElements) {
			str.add(m.getName());
		}
		bld.append("<b> Allowed childs: </b>");
		if ((childs != null) && (childs.length != 0)) {
			for (final String s : childs) {
				bld.append("<a href=\"./" + s + ".html\" >");
				bld.append(s);
				bld.append("</a>");
				bld.append(" ");
			}
		} else {
			bld.append(" None");
		}
		bld.append("</p><p>");

		bld.append("<b> Direct known sub elements: </b>");
		Collections.sort(str);
		if (!subElements.isEmpty()) {
			for (final String s : str) {
				bld.append("<a href=\"./" + s + ".html\" >");
				bld.append(s);
				bld.append("</a>");
				bld.append(" ");
			}
		} else {
			bld.append(" None");
		}
		bld.append("</p>");
		final DocumentationContribution documentationContribution = element
				.getDocumentationContribution();
		if (documentationContribution != null) {
			final String contents = documentationContribution.getContents();
			if (contents != null) {
				bld.append(contents);
			}
		}
		final HashMap<String, ArrayList<AttributeModel>> groups = new HashMap<String, ArrayList<AttributeModel>>();
		for (final AttributeModel m : properties) {
			String groupName = m.getGroup().trim();
			final boolean b = (groupName != null) && (groupName.length() > 0);
			if (b) {
				final GenericRegistryObject genericRegistryObject = GroupRegistry
						.getRegistry().get(groupName);
				if (genericRegistryObject != null) {
					groupName = genericRegistryObject.getName()
							+ " attributes summary";
				}
			} else {
				groupName = "Uncategorized attributes summary";
			}
			ArrayList<AttributeModel> arrayList = groups.get(groupName);
			if (arrayList == null) {
				arrayList = new ArrayList<AttributeModel>();
				groups.put(groupName, arrayList);
			}
			arrayList.add(m);
		}
		final ArrayList<String> arrayList = new ArrayList<String>(groups
				.keySet());
		Collections.sort(arrayList);
		for (final String s : arrayList) {
			final ArrayList<AttributeModel> names2 = groups.get(s);

			appendAttributeGroup(element, bld, names2, s);
			bld.append("<br>");
		}
		final ArrayList<AttributeModel> properties2 = element.getProperties();
		for (final String s : arrayList) {
			final ArrayList<AttributeModel> sma = groups.get(s);
			sma.retainAll(properties2);
			if (sma.isEmpty()) {
				continue;
			}
			bld.append("<p>");
			bld
					.append("<table CELLPADDING=\"3\" CELLSPACING=\"0\" width=\"100%\" border=\"1\"");
			String ls = s;
			if (s.endsWith("summary")) {
				ls = s.substring(0, s.length() - "summary".length());

			}
			bld
					.append("<tr BGCOLOR=\"#CCCCFF\"><td colspan=\"2\" valign=\"bottom\"><FONT SIZE=\"+2\" ><B>"
							+ ls + " details </B></FONT></td></tr></table>");

			final ArrayList<String> sm = new ArrayList<String>();
			for (final AttributeModel m : sma) {
				sm.add(m.getName());
			}
			for (final String a : sm) {
				final AttributeModel attribute = element.getProperty(a);
				if (attribute != null) {
					bld.append("<p>");
					bld.append("<h3><a name=\"" + attribute.getName() + "\">"
							+ attribute.getName() + "</a></h3>");
					String typeSpecialization = attribute
							.getTypeSpecialization();
					if ((typeSpecialization == null)
							|| (typeSpecialization.length() == 0)) {
						typeSpecialization = "None";
					}
					bld.append("Type: " + attribute.getType()
							+ " Type Specialization: " + typeSpecialization
							+ "<br>");
					bld.append("Required: " + attribute.isRequired() + "<br>");
					bld.append("Translatable: " + attribute.isTranslatable()
							+ "<br>");
					final DocumentationContribution documentationContribution1 = attribute
							.getDocumentationContribution();
					bld.append("<p>");
					if (documentationContribution1 != null) {
						final String contents = documentationContribution1
								.getContents();
						if (contents != null) {
							bld.append(contents);
						}
					}
					bld.append("</p>");
					bld.append("</p>");
					bld.append("<hr>");
				}
			}
			bld.append("</p>");
		}
		return bld.toString();
	}

	private static String getSummary(NameSpaceContributionModel object) {
		final StringBuilder bld = new StringBuilder();
		appendTag(bld, "h2", "Namespace: " + object.getName() + "("
				+ object.getUrl() + ")");
		bld.append("<p>" + object.getDescription() + "</p>");
		final Iterable<String> names = object.getElementNames();

		final HashMap<String, ArrayList<String>> groups = new HashMap<String, ArrayList<String>>();
		for (final String s : names) {
			final ElementModel element = object.getElement(s);
			if (element == null) {
				continue;
			}

			String groupName = element.getGroup().trim();
			final boolean b = (groupName != null) && (groupName.length() > 0);
			if (b) {
				final GenericRegistryObject genericRegistryObject = GroupRegistry
						.getRegistry().get(groupName);
				if (genericRegistryObject != null) {
					groupName = genericRegistryObject.getName() + "";
				}
			} else {
				groupName = "Uncategorized elements";
			}
			ArrayList<String> arrayList = groups.get(groupName);
			if (arrayList == null) {
				arrayList = new ArrayList<String>();
				groups.put(groupName, arrayList);
			}
			arrayList.add(element.getName());

		}
		final ArrayList<String> arrayList = new ArrayList<String>(groups
				.keySet());
		Collections.sort(arrayList);
		for (final String s : arrayList) {
			final ArrayList<String> names2 = groups.get(s);
			Collections.sort(names2);
			appendGroup(object, bld, names2, s);
			bld.append("<br>");
		}
		return bld.toString();
	}

	private static void appendAttributeGroup(ElementModel object,
			StringBuilder bld, ArrayList<AttributeModel> attrs, String groupName) {
		final HashSet<AttributeModel> properties = new HashSet<AttributeModel>(
				object.getProperties());
		HashSet<AttributeModel> restrictedAttributes = object.getRestrictedAttributes();
		final ArrayList<AttributeModel> defined = new ArrayList<AttributeModel>();
		final HashMap<ElementModel, ArrayList<AttributeModel>> inherited = new HashMap<ElementModel, ArrayList<AttributeModel>>();
		for (final AttributeModel m : attrs) {
			if(restrictedAttributes.contains(m)){
				continue;
			}
			if (properties.contains(m)) {
				defined.add(m);
			} else {
				ArrayList<AttributeModel> arrayList = inherited.get(m
						.getOwner());
				if (arrayList == null) {
					arrayList = new ArrayList<AttributeModel>();
					inherited.put((ElementModel) m.getOwner(), arrayList);
				}
				arrayList.add(m);
			}
		}

		ArrayList<String> names = new ArrayList<String>();
		for (final AttributeModel m : defined) {
			names.add(m.getName());
		}
		Collections.sort(names);
		bld.append("<p>");
		bld
				.append("<table CELLPADDING=\"3\" CELLSPACING=\"0\" width=\"100%\" border=\"1\"");
		bld
				.append("<tr BGCOLOR=\"#CCCCFF\"><td colspan=\"2\" valign=\"bottom\"><FONT SIZE=\"+2\" ><B>"
						+ groupName + "</B></FONT></td></tr>");
		for (final String s : names) {
			final AttributeModel element = object.getProperty(s);
			if (element == null) {
				continue;
			}
			bld.append("<tr BGCOLOR=\"white\" ><td witdh=\"20%\">");
			final String name = element.getName();
			bld.append("<a href=\"#" + name + "\">");
			bld.append(name);
			bld.append("</a>");
			bld.append("</td><td>");
			bld.append(element.getDescription());
			bld.append("</td></tr>");
		}
		bld.append("</table>");
		final ArrayList<ElementModel> supers = new ArrayList<ElementModel>(
				inherited.keySet());
		Collections.sort(supers, new Comparator<ElementModel>() {

			
			public int compare(ElementModel o1, ElementModel o2) {
				if (o1.getAllSuperElements().contains(o2)) {
					return -1;
				}
				if (o2.getAllSuperElements().contains(o1)) {
					return 1;
				}
				return o1.getName().compareTo(o2.getName());
			}
		});
		for (final ElementModel m : supers) {
			bld.append("<br/>");
			bld
					.append("<table CELLPADDING=\"3\" CELLSPACING=\"0\" width=\"100%\" border=\"1\"");
			bld
					.append("<tr BGCOLOR=\"#EEEEFF\"><td colspan=\"2\" valign=\"bottom\"><FONT SIZE=\"+1\" ><B>"
							+ "Attributes inherited from "
							+ m.getName()
							+ "</B></FONT></td></tr>");
			bld.append("<tr BGCOLOR=\"white\" ><td witdh=\"20%\">");
			final ArrayList<AttributeModel> arrayList = inherited.get(m);
			names = new ArrayList<String>();
			for (final AttributeModel ma : arrayList) {
				names.add(ma.getName());
			}
			Collections.sort(names);
			for (final String s : names) {
				// AttributeModel element = object.getProperty(s);
				// if (element == null) {
				// continue;
				// }
				final String name = s;
				bld
						.append("<a href=\"" + m.getName() + ".html#" + name
								+ "\">");
				bld.append(name);
				bld.append("</a>");
				bld.append(' ');
				;
			}
			bld.append("</td></tr>");
			bld.append("</table>");
		}
		bld.append("</p>");
	}

	private static void appendGroup(NameSpaceContributionModel object,
			StringBuilder bld, Iterable<String> names, String groupName) {
		bld.append("<p>");
		bld
				.append("<table CELLPADDING=\"3\" CELLSPACING=\"0\" width=\"100%\" border=\"1\"");
		bld
				.append("<tr BGCOLOR=\"#CCCCFF\"><td colspan=\"2\" valign=\"bottom\"><FONT SIZE=\"+2\" ><B>"
						+ groupName + "</B></FONT></td></tr>");
		for (final String s : names) {
			final ElementModel element = object.getElement(s);
			if (element == null) {
				continue;
			}
			bld.append("<tr BGCOLOR=\"white\" ><td witdh=\"20%\">");
			final String name = element.getName();
			bld.append("<a href=\"" + name + ".html\">");
			bld.append(name);
			bld.append("</a>");
			bld.append("</td><td>");
			bld.append(element.getDescription());
			bld.append("</td></tr>");
		}
		bld.append("</table>");
		bld.append("</p>");
	}

	private static void appendTag(StringBuilder bld, String name, String string) {
		bld.append("<");
		bld.append(name);
		bld.append(">");
		bld.append(string);
		bld.append("</");
		bld.append(name);
		bld.append(">");
	}

	static void writeToFile(IFile file, String content) {
		if (!file.exists()) {
			try {
				file.create(
						new ByteArrayInputStream(content.getBytes("UTF-8")),
						true, null);
			} catch (final UnsupportedEncodingException e) {
				Activator.log(e);
			} catch (final CoreException e) {
				Activator.log(e);
			}
		} else {
			try {
				file.setContents(new ByteArrayInputStream(content
						.getBytes("UTF-8")), true, true, null);
			} catch (final UnsupportedEncodingException e) {
				Activator.log(e);
			} catch (final CoreException e) {
				Activator.log(e);
			}
		}
	}
}
