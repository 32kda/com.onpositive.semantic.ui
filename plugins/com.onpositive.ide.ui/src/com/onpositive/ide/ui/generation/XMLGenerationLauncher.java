package com.onpositive.ide.ui.generation;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.ObjectPluginAction;

import com.onpositive.commons.Activator;
import com.onpositive.commons.xml.language.DOMEvaluator;
import com.onpositive.semantic.generator.model.CandidatesHolder;
import com.onpositive.semantic.generator.model.XMLStreamGenerator;
import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.ICommitListener;
import com.onpositive.semantic.model.ui.generic.IDisplayable;



public class XMLGenerationLauncher implements IObjectActionDelegate
{
	StructuredSelection selection = null;
	boolean wasCommit = false;

	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{
		// TODO Auto-generated method stub
		
	}

	public void run(IAction action)
	{
		if (action instanceof ObjectPluginAction)
		{
			if (selection == null) return;
			wasCommit = false;
			try
			{
				IFile classFile = (IFile)((StructuredSelection)selection).getFirstElement();
				ICompilationUnit p=(ICompilationUnit) JavaCore.create(classFile);
				String source = p.getSource();
				
				   // creation of DOM/AST from a ICompilationUnit
				ASTParser parser = ASTParser.newParser(AST.JLS3);
				parser.setResolveBindings(true);
				parser.setSource(p);
				CompilationUnit astRoot = (CompilationUnit) parser.createAST(null);
				List types = astRoot.types();				
				CandidatesHolder candidatesHolder = new CandidatesHolder((TypeDeclaration) types.get(0));
				
				
				final Binding context = new Binding(candidatesHolder);
				context.addCommitListener(new ICommitListener()
				{
					
					public void commitPerformed(ICommand command)
					{
						setWasCommit(true);						
					}					
				});
				final IDisplayable evaluateLocalPluginResource = (IDisplayable) DOMEvaluator
				.getInstance().evaluateLocalPluginResource(
						XMLGenerationLauncher.class,
						"WizardPropertiesSelectionPage.dlf", context); //$NON-NLS-1$
				evaluateLocalPluginResource.openWidget();
				
				context.dispose();
				if (wasCommit)
				{
					StreamResult result = new XMLStreamGenerator(candidatesHolder).serializeToXML();				
					String savePath = candidatesHolder.getSavePath();
					
					IJavaElement parent = p.getParent();
					while (parent != null && !(parent instanceof IJavaProject)) parent = parent.getParent();
					
					IProject curProject = (IProject) parent.getResource();				
	
					if (savePath != null)
					{
						writeFile(curProject,savePath,result.getOutputStream().toString());
						String targetClassName = candidatesHolder.getQualifiedTargetClassName();					
						addExtension(curProject, savePath, targetClassName);
					}
				}
			}
			catch (Exception e) {
				Activator.log(e);
				e.printStackTrace();
			}
			
			}
 		
	}

	protected void setWasCommit(boolean b)
	{
		wasCommit = b;		
	}

	/**
	 * Adds extension to widgetRegistry of plugin.xml
	 * @param curProject
	 * @param targetClassName 
	 * @param savePath 
	 */
	protected void addExtension(IProject curProject, String savePath, String targetClassName)
	{
		if (savePath.lastIndexOf('/') > -1) savePath = savePath.substring(savePath.lastIndexOf('/') + 1);
		String defaultContents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
		 "<?eclipse version=\"3.2\"?>\n " +
		 "<plugin>\n</plugin>";				
		String cnt = getFileContents(curProject,"plugin.xml",defaultContents);
		String insertedContents = "<extension\n" +
								  "		point=\"com.onpositive.semantic.ui.widgetRegistry" + "\">\n" +
								  "		<widget\n" +		
								  "			id = \"" + "id_" + targetClassName + "\"\n" +
								  "			targetClass = \"" + targetClassName + "\"\n" +
								  "			resource = \"" + savePath + "\">\n" + 
								  "		</widget>\n" +
								  "</extension>\n";
		cnt = insertStringAtPoint(cnt,"</plugin",insertedContents );
		writeFile(curProject,"plugin.xml", cnt);
	}

	public void selectionChanged(IAction action, ISelection selection)
	{
		if (selection instanceof StructuredSelection) this.selection = (StructuredSelection) selection;
		else this.selection = null;
	}
	
	public void writeFile(IProject project, String path, String contents)
	{
		if (path.lastIndexOf('/') > -1) path = path.substring(path.lastIndexOf('/') + 1);
		IFile file = project.getFile(path);
		byte[] array = contents.getBytes();
		try
		{
			if (!file.exists()) file.create(new ByteArrayInputStream(array), true, null);
			else file.setContents(new ByteArrayInputStream(array), true, false, null);
		} catch (CoreException e)
		{
			Activator.log(e);
		}
	}
	
	public String getFileContents(IProject project, String path, String defaultContents)
	{
		if (path.lastIndexOf('/') > -1) path = path.substring(path.lastIndexOf('/'));
		IFile file = project.getFile(path);	
		InputStream stream = null;
		try
		{
			file.refreshLocal(IResource.DEPTH_ZERO, null);
			if (!file.exists())
				file.create(new ByteArrayInputStream(defaultContents.getBytes()), true, null);
			stream = file.getContents();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
			String str = "";
			while (bufferedReader.ready()) str = str + bufferedReader.readLine() + "\n";
			return str;
		} catch (Exception e)
		{
			Activator.log(e);
		}
		return null;
	}
	
	/**
	 * Inserts some string into another before some substring
	 * @param contents String to insert into
	 * @param whereToInsert substring, BEFORE LAST index of what contents should be inserted
	 * @param whatToInsert What to inserted
	 * @return result string
	 */
	public static String insertStringAtPoint(String contents, String whereToInsert, String whatToInsert)
	{		
		int insertionIndex = contents.lastIndexOf(whereToInsert);
		if (insertionIndex > -1)
		{
			String res = contents.substring(0,insertionIndex) + whatToInsert + contents.substring(insertionIndex);
			return res;
		}
		return contents;
	}
}
