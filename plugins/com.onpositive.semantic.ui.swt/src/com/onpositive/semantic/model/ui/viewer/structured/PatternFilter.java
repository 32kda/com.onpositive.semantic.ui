package com.onpositive.semantic.model.ui.viewer.structured;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.onpositive.semantic.model.realm.IFilter;
import com.onpositive.semantic.model.realm.ISimpleChangeListener;
import com.onpositive.semantic.model.tree.ITreeNode;
import com.onpositive.semantic.model.ui.property.editors.structured.IRealmContentProvider;
import com.onpositive.semantic.model.ui.viewer.structured.StringMatcher.Position;

public class PatternFilter extends ViewerFilter implements IFilter {

	private StringMatcher pattern;
	private ViewerFilter[] filters;
	private ColumnViewer viewer;
	private TreePath[] pathes;
	private boolean installed;

	public PatternFilter() {

	}

	public synchronized StringMatcher getPattern() {
		return this.pattern;
	}

	public synchronized void setPattern(String pattern) {
		this.pattern = new StringMatcher("*" + pattern + "*", true, false); //$NON-NLS-1$ //$NON-NLS-2$
		if ((pattern == null) || (pattern.length() == 0)) {
			if (this.installed) {
				this.viewer.getControl().setRedraw(false);

				this.removeFilter();
				if (this.pathes != null) {
					try {
						if (this.viewer instanceof TreeViewer) {
							((TreeViewer) this.viewer)
									.setExpandedTreePaths(this.pathes);
						}
					} catch (final Exception e) {
					}
				}
				this.viewer.getControl().setRedraw(true);
				this.installed = false;
			}
		} else {
			this.filters = this.viewer.getFilters();
			this.expanding.clear();
			this.viewer.getControl().setRedraw(false);
			try {
				if (!this.installed) {
					if (this.viewer instanceof TreeViewer) {
						this.pathes = ((TreeViewer) this.viewer)
								.getExpandedTreePaths();
					}
					this.addFilter();
					this.installed = true;
				} else {
					for (final ISimpleChangeListener<IFilter> l : this.listeners) {
						l.changed(this, null);
					}
					this.viewer.refresh();
				}
				for (final Object o : new HashSet<Object>(this.expanding)) {
					((TreeViewer) this.viewer).expandToLevel(o, 1);
				}
			} finally {
				this.expanding.clear();
				this.viewer.getControl().setRedraw(true);
			}
		}
		// System.out.println("Refresh:" + (l1 - l0));
	}

	private void addFilter() {
		final IContentProvider pr = this.viewer.getContentProvider();
		if (pr instanceof IRealmContentProvider) {
			final IRealmContentProvider ca = (IRealmContentProvider) pr;
			ca.addFilter(this);
		}
		this.viewer.addFilter(this);
	}

	private void removeFilter() {
		final IContentProvider pr = this.viewer.getContentProvider();
		if (pr instanceof IRealmContentProvider) {
			final IRealmContentProvider ca = (IRealmContentProvider) pr;
			ca.removeFilter(this);
		}
		this.viewer.removeFilter(this);
	}

	private final HashSet<Object> expanding = new HashSet<Object>();
	private final HashSet<ISimpleChangeListener<IFilter>> listeners = new HashSet<ISimpleChangeListener<IFilter>>();

	public boolean select(Viewer viewer, Object parentElement, Object element) {
		return this.accept(element);
	}

	private boolean internalMatch(String text) {
		return this.pattern.match(text);
		// for (String s : getWords(text)) {
		// if (pattern.match(s))
		// return true;
		// }
		// return false;
	}

	public PatternFilter(ColumnViewer viewer) {
		super();
		this.viewer = viewer;
	}

	public Position indexOf(String string) {
		final BreakIterator iter = BreakIterator.getWordInstance();
		iter.setText(string);
//		int i = iter.first();
		// while ((i != java.text.BreakIterator.DONE) && (i < string.length()))
		// {
		// int j = iter.following(i);
		// if (j == java.text.BreakIterator.DONE) {
		// j = string.length();
		// }
		// // match the word
		// if (Character.isLetterOrDigit(string.charAt(i))) {
		final Position find2 = this.pattern.find(string, 0, string.length());
		if (find2 != null) {
			return find2;
		}
		// }
		// i = j;
		// }
		return null;
	}

	/**
	 * Take the given filter text and break it down into words using a
	 * BreakIterator.
	 * 
	 * @param text
	 * @return an array of words
	 */
	@SuppressWarnings( { "unchecked", "unused" })
	private String[] getWords(String text) {
		final List words = new ArrayList();
		// Break the text up into words, separating based on whitespace and
		// common punctuation.
		// Previously used String.split(..., "\\W"), where "\W" is a regular
		// expression (see the Javadoc for class Pattern).
		// Need to avoid both String.split and regular expressions, in order to
		// compile against JCL Foundation (bug 80053).
		// Also need to do this in an NL-sensitive way. The use of BreakIterator
		// was suggested in bug 90579.
		final BreakIterator iter = BreakIterator.getWordInstance();
		iter.setText(text);
		int i = iter.first();
		while ((i != java.text.BreakIterator.DONE) && (i < text.length())) {
			int j = iter.following(i);
			if (j == java.text.BreakIterator.DONE) {
				j = text.length();
			}
			// match the word
			if (Character.isLetterOrDigit(text.charAt(i))) {
				final String word = text.substring(i, j);
				words.add(word);
			}
			i = j;
		}
		return (String[]) words.toArray(new String[words.size()]);
	}
	
	HashSet<Object>onStack=new HashSet<Object>();

	@SuppressWarnings("unchecked")
	public synchronized boolean accept(Object element) {		
		if (onStack.contains(element)){
			return false;
		}
		onStack.add(element);
		try{
		if (this.viewer instanceof TreeViewer) {
			final TreeViewer viewer2 = (TreeViewer) this.viewer;

			final IContentProvider contentProvider = (viewer2)
					.getContentProvider();
			if (element instanceof ITreeNode<?>) {
				final ITreeNode<Object> m = (ITreeNode<Object>) element;
				if (m.hasChildren()) {
					l2: for (final Object a : m.getChildren()) {
						{
							for (final ViewerFilter f : this.filters) {
								if (!f.select(this.viewer, element, a)) {
									continue l2;
								}
							}
							if (!this.installed) {
								if (!this.select(this.viewer, element, a)) {
									continue l2;
								}
							}
							if (this.pattern.fLength > 4) {
								this.expanding.add(element);
							}
							return true;
						}
					}
				}
			} else {
				final ITreeContentProvider cp = (ITreeContentProvider) contentProvider;

				if (cp.hasChildren(element)) {
					l2: for (final Object a : cp.getChildren(element)) {
						{
							for (final ViewerFilter f : this.filters) {
								if (!f.select(this.viewer, element, a)) {
									continue l2;
								}
							}
							if (!this.installed) {
								if (!this.select(this.viewer, element, a)) {
									continue l2;
								}
							}
							if (this.pattern.fLength > 4) {
								this.expanding.add(element);
							}
							return true;
						}
					}
				}
			}}		
		}
		finally{
			onStack.remove(element);
		}
		final ILabelProvider labelProvider = (ILabelProvider) (this.viewer)
				.getLabelProvider();
		final String text = labelProvider.getText(element);
		return this.internalMatch(text);
	}

	@SuppressWarnings("unchecked")
	public void addFilterListener(
			ISimpleChangeListener<? extends IFilter> listener) {
		this.listeners.add((ISimpleChangeListener<IFilter>) listener);
	}

	public void removeFilterListener(
			ISimpleChangeListener<? extends IFilter> listener) {
		this.listeners.remove(listener);
	}
}