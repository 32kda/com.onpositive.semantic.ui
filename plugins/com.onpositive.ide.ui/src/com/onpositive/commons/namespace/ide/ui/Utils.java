package com.onpositive.commons.namespace.ide.ui;

import java.text.MessageFormat;
import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import com.onpositive.semantic.model.api.status.CodeAndMessage;

public class Utils {

	public static CodeAndMessage isOnClasspath(String fullyQualifiedName,
			IProject project, String requireImplement) {
		return isOnClasspath(fullyQualifiedName, JavaCore.create(project),
				requireImplement);
	}

	public static CodeAndMessage isOnClasspath(String fullyQualifiedName,
			IJavaProject project, String requireImplement) {
		final int lastIndexOf = requireImplement.lastIndexOf('.');
		final String[] sig = new String[] {
				requireImplement.substring(0, lastIndexOf),
				requireImplement.substring(lastIndexOf + 1) };
		if (fullyQualifiedName.indexOf('$') != -1) {
			fullyQualifiedName = fullyQualifiedName.replace('$', '.');
		}
		try {
			final IType type = project.findType(fullyQualifiedName);
			final boolean b = (type != null) && type.exists();
			if (b) {
				final boolean subType = isSubType(project, sig, type);
				if (!subType) {
					return CodeAndMessage.errorMessage(MessageFormat.format(
							"class {0} is not descendant of {1}",
							fullyQualifiedName, requireImplement));
				}
				return CodeAndMessage.OK_MESSAGE;
			}
			return CodeAndMessage.errorMessage(MessageFormat.format(
					"class {0} does not exists on classpath of project {1}",
					fullyQualifiedName, project.getElementName()));
		} catch (final JavaModelException e) {
		}
		return CodeAndMessage.errorMessage("Java Model Exception");
	}

	private static boolean isSubType(IJavaProject project, String[] sig,
			IType type) throws JavaModelException {

		final String superclassName = type.getSuperclassTypeSignature();

		if (superclassName != null) {

			final String[][] resolveType = type.resolveType(superclassName);
			if (Arrays.equals(resolveType, sig)) {
				return true;
			}
			final IType stype = project.findType(superclassName);
			if ((stype != null) && !stype.exists()) {
				if (isSubType(project, sig, stype)) {
					return true;
				}
			}
		}
		final String[] superInterfaceNames = type.getSuperInterfaceNames();
		if (superInterfaceNames != null) {
			for (final String s : superInterfaceNames) {
				final String[][] resolveType = type.resolveType(s);
				if (resolveType != null) {
					for (final String[] signature : resolveType) {
						if (Arrays.equals(signature, sig)) {
							return true;
						}
						final IType findType = project.findType(signature[0],
								signature[1]);
						if ((findType != null) && findType.exists()) {
							if (isSubType(project, sig, findType)) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
}
