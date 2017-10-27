package com.onpositive.semantic.ui.text;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.texteditor.AnnotationPreference;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import org.eclipse.jface.text.CursorLinePainter;
import org.eclipse.jface.text.IPainter;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.ITextViewerExtension4;
import org.eclipse.jface.text.MarginPainter;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationPainter;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.ICharacterPairMatcher;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.MatchingCharacterPainter;
import org.eclipse.jface.text.source.AnnotationPainter.IDrawingStrategy;

/**
 * Support class used by text editors to draw and update decorations on the
 * source viewer and its rulers. An instance of this class is independent of a
 * certain editor and must be configured with the needed preference keys and
 * helper objects before it can be used.
 * <p>
 * Once configured, an instance may be installed (see
 * {@link #install(IPreferenceStore) install}) on a preference store, from then
 * on monitoring the configured preference settings and changing the respective
 * decorations. Calling {@link #uninstall() uninstall} will unregister the
 * listeners with the preferences store and must be called before changing the
 * preference store by another call to <code>install</code>.<br>
 * {@link #dispose() dispose} will uninstall the support and remove any
 * decorations from the viewer. It is okay to reuse a
 * <code>SourceViewerDecorationSupport</code> instance after disposing it.
 * </p>
 * <p>
 * <code>SourceViewerDecorationSupport</code> can draw the following
 * decorations:
 * <ul>
 * <li>matching character highlighting,</li>
 * <li>current line highlighting,</li>
 * <li>print margin, and</li>
 * <li>annotations.</li>
 * </ul>
 * Annotations are managed for the overview ruler and also drawn onto the text
 * widget by an {@link org.eclipse.jface.text.source.AnnotationPainter
 * AnnotationPainter} instance.
 * </p>
 * <p>
 * Subclasses may add decorations but should adhere to the lifecyle described
 * above.
 * </p>
 * 
 * @see org.eclipse.jface.text.source.AnnotationPainter
 * @since 2.1
 */
public class SourceViewerDecorationSupport {

	/**
	 * Underline drawing strategy.
	 * 
	 * @since 3.0
	 */
	private static final class UnderlineDrawingStrategy implements
			IDrawingStrategy {

		/*
		 * @see
		 * org.eclipse.jface.text.source.AnnotationPainter.IDrawingStrategy#
		 * draw(org.eclipse.jface.text.source.Annotation,
		 * org.eclipse.swt.graphics.GC, org.eclipse.swt.custom.StyledText, int,
		 * int, org.eclipse.swt.graphics.Color)
		 */
		public void draw(Annotation annotation, GC gc, StyledText textWidget,
				int offset, int length, Color color) {
			if (gc != null) {

				Rectangle bounds;
				if (length > 0) {
					bounds = textWidget.getTextBounds(offset, offset + length
							- 1);
				} else {
					final Point loc = textWidget.getLocationAtOffset(offset);
					bounds = new Rectangle(loc.x, loc.y, 1, textWidget
							.getLineHeight(offset));
				}

				final int y = bounds.y + bounds.height - 1;

				gc.setForeground(color);
				gc.drawLine(bounds.x, y, bounds.x + bounds.width, y);

			} else {
				textWidget.redrawRange(offset, length, true);
			}
		}
	}

	/**
	 * Draws a box around a given range.
	 * 
	 * @since 3.0
	 */
	private static class BoxDrawingStrategy implements IDrawingStrategy {
		/*
		 * @see
		 * org.eclipse.jface.text.source.AnnotationPainter.IDrawingStrategy#
		 * draw(org.eclipse.jface.text.source.Annotation,
		 * org.eclipse.swt.graphics.GC, org.eclipse.swt.custom.StyledText, int,
		 * int, org.eclipse.swt.graphics.Color)
		 */
		public void draw(Annotation annotation, GC gc, StyledText textWidget,
				int offset, int length, Color color) {

			if (length == 0) {
				fgIBeamStrategy.draw(annotation, gc, textWidget, offset,
						length, color);
				return;
			}

			if (gc != null) {

				Rectangle bounds;
				if (length > 0) {
					bounds = textWidget.getTextBounds(offset, offset + length
							- 1);
				} else {
					final Point loc = textWidget.getLocationAtOffset(offset);
					bounds = new Rectangle(loc.x, loc.y, 1, textWidget
							.getLineHeight(offset));
				}

				this.drawBox(gc, textWidget, color, bounds);

			} else {
				textWidget.redrawRange(offset, length, true);
			}
		}

		protected void drawBox(GC gc, StyledText textWidget, Color color,
				Rectangle bounds) {
			gc.setForeground(color);
			gc.drawRectangle(bounds.x, bounds.y, bounds.width - 1,
					bounds.height - 1);
		}
	}

	/**
	 * Dashed box drawing strategy.
	 * 
	 * @since 3.3
	 */
	private static final class DashedBoxDrawingStrategy extends
			BoxDrawingStrategy {
		/*
		 * @seeorg.eclipse.ui.texteditor.SourceViewerDecorationSupport.
		 * BoxDrawingStrategy#drawBox(org.eclipse.swt.graphics.GC,
		 * org.eclipse.swt.graphics.Color, org.eclipse.swt.graphics.Rectangle)
		 */
		protected void drawBox(GC gc, StyledText textWidget, Color color,
				Rectangle bounds) {
			// clean bg:
			gc.setForeground(textWidget.getBackground());
			gc.setLineStyle(SWT.LINE_SOLID);
			final int x = bounds.x;
			final int y = bounds.y;
			final int w = bounds.width - 1;
			final int h = bounds.height - 1;
			gc.drawRectangle(x, y, w, h);

			gc.setForeground(color);
			gc.setLineDash(new int[] { 3 });

			// gc.drawRectangle(x, y, w, h) is platform-dependent and can look
			// "animated"
			gc.drawLine(x, y, x + w, y);
			gc.drawLine(x, y + h, x + w, y + h);
			gc.drawLine(x, y, x, y + h);
			gc.drawLine(x + w, y, x + w, y + h);

			// RESET (same GC is passed around!):
			gc.setLineStyle(SWT.LINE_SOLID);
		}
	}

	/**
	 * Draws an iBeam at the given offset, the length is ignored.
	 * 
	 * @since 3.0
	 */
	private static final class IBeamStrategy implements IDrawingStrategy {

		/*
		 * @see
		 * org.eclipse.jface.text.source.AnnotationPainter.IDrawingStrategy#
		 * draw(org.eclipse.jface.text.source.Annotation,
		 * org.eclipse.swt.graphics.GC, org.eclipse.swt.custom.StyledText, int,
		 * int, org.eclipse.swt.graphics.Color)
		 */
		public void draw(Annotation annotation, GC gc, StyledText textWidget,
				int offset, int length, Color color) {
			if (gc != null) {

				final Point left = textWidget.getLocationAtOffset(offset);
				final int x1 = left.x;
				final int y1 = left.y;

				gc.setForeground(color);
				gc.drawLine(x1, y1, x1, left.y
						+ textWidget.getLineHeight(offset) - 1);

			} else {
				/*
				 * The length for IBeam's is always 0, which causes no redraw to
				 * occur in StyledText#redraw(int, int, boolean). We try to
				 * normally redraw at length of one, and up to the line start of
				 * the next line if offset is at the end of line. If at the end
				 * of the document, we redraw the entire document as the offset
				 * is behind any content.
				 */
				final int contentLength = textWidget.getCharCount();
				if (offset >= contentLength) {
					textWidget.redraw();
					return;
				}

				final char ch = textWidget.getTextRange(offset, 1).charAt(0);
				if ((ch == '\r') || (ch == '\n')) {
					// at the end of a line, redraw up to the next line start
					final int nextLine = textWidget.getLineAtOffset(offset) + 1;
					if (nextLine >= textWidget.getLineCount()) {
						/*
						 * Panic code: should not happen, as offset is not the
						 * last offset, and there is a delimiter character at
						 * offset.
						 */
						textWidget.redraw();
						return;
					}

					final int nextLineOffset = textWidget
							.getOffsetAtLine(nextLine);
					length = nextLineOffset - offset;
				} else {
					length = 1;
				}

				textWidget.redrawRange(offset, length, true);
			}
		}
	}

	private static final boolean USE_TEXT_STYLE_STRATEGIES = false;

//	/**
//	 * The box drawing strategy.
//	 * 
//	 * @since 3.0
//	 */
//	private static ITextStyleStrategy fgBoxStrategy = new AnnotationPainter.BoxStrategy(
//			SWT.BORDER_SOLID);
//
//	/**
//	 * The dashed box drawing strategy.
//	 * 
//	 * @since 3.3
//	 */
//	private static ITextStyleStrategy fgDashedBoxStrategy = new AnnotationPainter.BoxStrategy(
//			SWT.BORDER_DASH);

	/**
	 * The null drawing strategy.
	 * 
	 * @since 3.0
	 */
	private static IDrawingStrategy fgNullStrategy = new AnnotationPainter.NullStrategy();

	/**
	 * The underline drawing strategy.
	 * 
	 * @since 3.0
	 */
//	private static ITextStyleStrategy fgUnderlineStrategy = new AnnotationPainter.UnderlineStrategy(
//			SWT.UNDERLINE_SINGLE);

	/**
	 * The iBeam drawing strategy.
	 * 
	 * @since 3.0
	 */
	private static IDrawingStrategy fgIBeamStrategy = new IBeamStrategy();

	/**
	 * The squiggles drawing strategy.
	 * 
	 * @since 3.0
	 */
//	private static ITextStyleStrategy fgSquigglesStrategy = new AnnotationPainter.UnderlineStrategy(
//			SWT.UNDERLINE_SQUIGGLE);
//
//	/**
//	 * The error drawing strategy.
//	 * 
//	 * @since 3.4
//	 */
//	private static ITextStyleStrategy fgProblemUnderlineStrategy = new AnnotationPainter.UnderlineStrategy(
//			SWT.UNDERLINE_ERROR);

	/*
	 * @see IPropertyChangeListener
	 */
	private class FontPropertyChangeListener implements IPropertyChangeListener {
		/*
		 * @seeIPropertyChangeListener#propertyChange(org.eclipse.jface.util.
		 * PropertyChangeEvent)
		 */
		public void propertyChange(PropertyChangeEvent event) {
			if ((SourceViewerDecorationSupport.this.fMarginPainter != null)
					&& (SourceViewerDecorationSupport.this.fSymbolicFontName != null)
					&& SourceViewerDecorationSupport.this.fSymbolicFontName
							.equals(event.getProperty())) {
				SourceViewerDecorationSupport.this.fMarginPainter.initialize();
			}
		}
	}

	/** The viewer */
	private final ISourceViewer fSourceViewer;
	/** The viewer's overview ruler */
	private IOverviewRuler fOverviewRuler;
	/** The annotation access */
	private final IAnnotationAccess fAnnotationAccess;
	/** The shared color manager */
	private final ISharedTextColors fSharedTextColors;

	/** The editor's line painter */
	private CursorLinePainter fCursorLinePainter;
	/** The editor's margin ruler painter */
	private MarginPainter fMarginPainter;
	/** The editor's annotation painter */
	private AnnotationPainter fAnnotationPainter;
	/** The editor's peer character painter */
	private MatchingCharacterPainter fMatchingCharacterPainter;
	/** The character painter's pair matcher */
	private ICharacterPairMatcher fCharacterPairMatcher;

	/** Map with annotation type preference per annotation type */
	private final Map fAnnotationTypeKeyMap = new HashMap();
	/** Preference key for the cursor line highlighting */
	private String fCursorLinePainterEnableKey;
	/** Preference key for the cursor line background color */
	private String fCursorLinePainterColorKey;
	/** Preference key for the margin painter */
	private String fMarginPainterEnableKey;
	/** Preference key for the margin painter color */
	private String fMarginPainterColorKey;
	/** Preference key for the margin painter column */
	private String fMarginPainterColumnKey;
	/** Preference key for the matching character painter */
	private String fMatchingCharacterPainterEnableKey;
	/** Preference key for the matching character painter color */
	private String fMatchingCharacterPainterColorKey;
	/** The property change listener */
	private IPropertyChangeListener fPropertyChangeListener;
	/** The preference store */
	private IPreferenceStore fPreferenceStore;
	/** The symbolic font name */
	private String fSymbolicFontName;
	/** The font change listener */
	private FontPropertyChangeListener fFontPropertyChangeListener;
	/** Annotations, that shouldn't be drawn */
	protected HashSet<String> ignoredAnnotationTypes; 

	/**
	 * Creates a new decoration support for the given viewer.
	 * 
	 * @param sourceViewer
	 *            the source viewer
	 * @param overviewRuler
	 *            the viewer's overview ruler
	 * @param annotationAccess
	 *            the annotation access
	 * @param sharedTextColors
	 *            the shared text color manager
	 */
	public SourceViewerDecorationSupport(ISourceViewer sourceViewer,
			IOverviewRuler overviewRuler, IAnnotationAccess annotationAccess,
			ISharedTextColors sharedTextColors) {
		this.fSourceViewer = sourceViewer;
		this.fOverviewRuler = overviewRuler;
		this.fAnnotationAccess = annotationAccess;
		this.fSharedTextColors = sharedTextColors;
		ignoredAnnotationTypes = new HashSet<String>();
	}

	/**
	 * Installs this decoration support on the given preference store. It
	 * assumes that this support has completely been configured.
	 * 
	 * @param store
	 *            the preference store
	 */
	public void install(IPreferenceStore store) {

		this.fPreferenceStore = store;
		if (this.fPreferenceStore != null) {
			this.fPropertyChangeListener = new IPropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent event) {
					SourceViewerDecorationSupport.this
							.handlePreferenceStoreChanged(event);
				}
			};
			this.fPreferenceStore
					.addPropertyChangeListener(this.fPropertyChangeListener);
		}

		this.updateTextDecorations();
		this.updateOverviewDecorations();
	}

	/**
	 * Updates the text decorations for all configured annotation types.
	 */
	private void updateTextDecorations() {
		if (fAnnotationPainter == null) fAnnotationPainter = createAnnotationPainter();

		final StyledText widget = this.fSourceViewer.getTextWidget();
		if ((widget == null) || widget.isDisposed()) {
			return;
		}

		if (this.areMatchingCharactersShown()) {
			this.showMatchingCharacters();
		} else {
			this.hideMatchingCharacters();
		}

		if (this.isCursorLineShown()) {
			this.showCursorLine();
		} else {
			this.hideCursorLine();
		}

		if (this.isMarginShown()) {
			this.showMargin();
		} else {
			this.hideMargin();
		}

		final Iterator<Object> e = this.fAnnotationTypeKeyMap.keySet().iterator();
		while (e.hasNext()) {
			final Object type = e.next();
			// if (areAnnotationsHighlighted(type) || areAnnotationsShown(type))
			this.showAnnotations(type, true);
			// else
			// hideAnnotations(type, false);

		}
		this.updateAnnotationPainter();
	}

	/**
	 * Returns the annotation decoration style used for the show in text
	 * preference for a given annotation type.
	 * 
	 * @param annotationType
	 *            the annotation type being looked up
	 * @return the decoration style for <code>type</code> or <code>null</code>
	 *         if highlighting
	 * @since 3.0
	 */
	private Object getAnnotationDecorationType(Object annotationType) {
		if (this.areAnnotationsHighlighted(annotationType)) {
			return null;
		}

		if (this.areAnnotationsShown(annotationType)) {
			final AnnotationPreference info = (AnnotationPreference) this.fAnnotationTypeKeyMap
					.get(annotationType);
			if (info != null) {
				final String key = info.getTextStylePreferenceKey();
				if (key != null) {
					return this.fPreferenceStore.getString(key);
				}
				// legacy
				return AnnotationPreference.STYLE_SQUIGGLES;
			}
		}
		return AnnotationPreference.STYLE_NONE;
	}

	/**
	 * Updates the annotation overview for all configured annotation types.
	 */
	public void updateOverviewDecorations() {
		if (this.fOverviewRuler != null) {
			final Iterator e = this.fAnnotationTypeKeyMap.keySet().iterator();
			while (e.hasNext()) {
				final Object type = e.next();
				if (this.isAnnotationOverviewShown(type)) {
					this.showAnnotationOverview(type, false);
				} else {
					this.hideAnnotationOverview(type, false);
				}
			}
			this.fOverviewRuler.update();
		}
	}

	/**
	 * Uninstalls this support from the preference store it has previously been
	 * installed on. If there is no such preference store, this call is without
	 * effect.
	 */
	public void uninstall() {

		if (this.fPreferenceStore != null) {
			this.fPreferenceStore
					.removePropertyChangeListener(this.fPropertyChangeListener);
			this.fPropertyChangeListener = null;
			this.fPreferenceStore = null;
		}
	}

	/**
	 * Disposes this decoration support. Internally calls <code>uninstall</code>
	 * .
	 */
	public void dispose() {
		this.uninstall();
		this.updateTextDecorations();
		this.updateOverviewDecorations();

		if (this.fFontPropertyChangeListener != null) {
			JFaceResources.getFontRegistry().removeListener(
					this.fFontPropertyChangeListener);
			this.fFontPropertyChangeListener = null;
		}

		this.fOverviewRuler = null;

		// Painters got disposed in updateTextDecorations() or by the
		// PaintManager
		this.fMatchingCharacterPainter = null;
		this.fCursorLinePainter = null;
		this.fAnnotationPainter = null;
		this.fCursorLinePainter = null;
		this.fMarginPainter = null;

		if (this.fAnnotationTypeKeyMap != null) {
			this.fAnnotationTypeKeyMap.clear();
		}
	}

	/**
	 * Sets the character pair matcher for the matching character painter.
	 * 
	 * @param pairMatcher
	 */
	public void setCharacterPairMatcher(ICharacterPairMatcher pairMatcher) {
		this.fCharacterPairMatcher = pairMatcher;
	}

	/**
	 * Sets the preference keys for the annotation painter.
	 * 
	 * @param type
	 *            the annotation type
	 * @param colorKey
	 *            the preference key for the color
	 * @param editorKey
	 *            the preference key for the presentation in the text area
	 * @param overviewRulerKey
	 *            the preference key for the presentation in the overview ruler
	 * @param layer
	 *            the layer
	 */
	public void setAnnotationPainterPreferenceKeys(Object type,
			String colorKey, String editorKey, String overviewRulerKey,
			int layer) {
		final AnnotationPreference info = new AnnotationPreference(type,
				colorKey, editorKey, overviewRulerKey, layer);
		this.fAnnotationTypeKeyMap.put(type, info);
	}

	/**
	 * Sets the preference info for the annotation painter.
	 * 
	 * @param info
	 *            the preference info to be set
	 */
	public void setAnnotationPreference(AnnotationPreference info) {
		this.fAnnotationTypeKeyMap.put(info.getAnnotationType(), info);
	}

	/**
	 * Sets the preference keys for the cursor line painter.
	 * 
	 * @param enableKey
	 *            the preference key for the cursor line painter
	 * @param colorKey
	 *            the preference key for the color used by the cursor line
	 *            painter
	 */
	public void setCursorLinePainterPreferenceKeys(String enableKey,
			String colorKey) {
		this.fCursorLinePainterEnableKey = enableKey;
		this.fCursorLinePainterColorKey = colorKey;
	}

	/**
	 * Sets the preference keys for the margin painter.
	 * 
	 * @param enableKey
	 *            the preference key for the margin painter
	 * @param colorKey
	 *            the preference key for the color used by the margin painter
	 * @param columnKey
	 *            the preference key for the margin column
	 */
	public void setMarginPainterPreferenceKeys(String enableKey,
			String colorKey, String columnKey) {
		this.fMarginPainterEnableKey = enableKey;
		this.fMarginPainterColorKey = colorKey;
		this.fMarginPainterColumnKey = columnKey;
	}

	/**
	 * Sets the preference keys for the matching character painter.
	 * 
	 * @param enableKey
	 *            the preference key for the matching character painter
	 * @param colorKey
	 *            the preference key for the color used by the matching
	 *            character painter
	 */
	public void setMatchingCharacterPainterPreferenceKeys(String enableKey,
			String colorKey) {
		this.fMatchingCharacterPainterEnableKey = enableKey;
		this.fMatchingCharacterPainterColorKey = colorKey;
	}

	/**
	 * Sets the symbolic font name that is used for computing the margin width.
	 * 
	 * @param symbolicFontName
	 */
	public void setSymbolicFontName(String symbolicFontName) {
		this.fSymbolicFontName = symbolicFontName;
	}

	/**
	 * Returns the annotation preference for the given key.
	 * 
	 * @param preferenceKey
	 *            the preference key string
	 * @return the annotation preference
	 */
	private AnnotationPreference getAnnotationPreferenceInfo(
			String preferenceKey) {
		final Iterator e = this.fAnnotationTypeKeyMap.values().iterator();
		while (e.hasNext()) {
			final AnnotationPreference info = (AnnotationPreference) e.next();
			if ((info != null) && info.isPreferenceKey(preferenceKey)) {
				return info;
			}
		}
		return null;
	}

	/*
	 * @see AbstractTextEditor#handlePreferenceStoreChanged(PropertyChangeEvent)
	 */
	protected void handlePreferenceStoreChanged(PropertyChangeEvent event) {

		final String p = event.getProperty();

		if ((this.fMatchingCharacterPainterEnableKey != null)
				&& this.fMatchingCharacterPainterEnableKey.equals(p)
				&& (this.fCharacterPairMatcher != null)) {
			if (this.areMatchingCharactersShown()) {
				this.showMatchingCharacters();
			} else {
				this.hideMatchingCharacters();
			}
			return;
		}

		if ((this.fMatchingCharacterPainterColorKey != null)
				&& this.fMatchingCharacterPainterColorKey.equals(p)) {
			if (this.fMatchingCharacterPainter != null) {
				this.fMatchingCharacterPainter.setColor(this
						.getColor(this.fMatchingCharacterPainterColorKey));
				this.fMatchingCharacterPainter.paint(IPainter.CONFIGURATION);
			}
			return;
		}

		if ((this.fCursorLinePainterEnableKey != null)
				&& this.fCursorLinePainterEnableKey.equals(p)) {
			if (this.isCursorLineShown()) {
				this.showCursorLine();
			} else {
				this.hideCursorLine();
			}
			return;
		}

		if ((this.fCursorLinePainterColorKey != null)
				&& this.fCursorLinePainterColorKey.equals(p)) {
			if (this.fCursorLinePainter != null) {
				this.hideCursorLine();
				this.showCursorLine();
			}
			return;
		}

		if ((this.fMarginPainterEnableKey != null)
				&& this.fMarginPainterEnableKey.equals(p)) {
			if (this.isMarginShown()) {
				this.showMargin();
			} else {
				this.hideMargin();
			}
			return;
		}

		if ((this.fMarginPainterColorKey != null)
				&& this.fMarginPainterColorKey.equals(p)) {
			if (this.fMarginPainter != null) {
				this.fMarginPainter.setMarginRulerColor(this
						.getColor(this.fMarginPainterColorKey));
				this.fMarginPainter.paint(IPainter.CONFIGURATION);
			}
			return;
		}

		if ((this.fMarginPainterColumnKey != null)
				&& this.fMarginPainterColumnKey.equals(p)) {
			if ((this.fMarginPainter != null)
					&& (this.fPreferenceStore != null)) {
				this.fMarginPainter.setMarginRulerColumn(this.fPreferenceStore
						.getInt(this.fMarginPainterColumnKey));
				this.fMarginPainter.paint(IPainter.CONFIGURATION);
			}
			return;
		}

		final AnnotationPreference info = this.getAnnotationPreferenceInfo(p);
		if (info != null) {

			if (info.getColorPreferenceKey().equals(p)) {
				final Color color = this.getColor(info.getColorPreferenceKey());
				if (this.fAnnotationPainter != null) {
					this.fAnnotationPainter.setAnnotationTypeColor(info
							.getAnnotationType(), color);
					this.fAnnotationPainter.paint(IPainter.CONFIGURATION);
				}
				this
						.setAnnotationOverviewColor(info.getAnnotationType(),
								color);
				return;
			}

			final Object type = info.getAnnotationType();
			if ((info.getTextPreferenceKey().equals(p) || ((info
					.getTextStylePreferenceKey() != null) && info
					.getTextStylePreferenceKey().equals(p)))
					|| ((info.getHighlightPreferenceKey() != null) && info
							.getHighlightPreferenceKey().equals(p))) {
				if (this.areAnnotationsHighlighted(type)
						|| this.areAnnotationsShown(type)) {
					this.showAnnotations(type, true);
				} else {
					this.hideAnnotations(type, true);
				}
				return;
			}

			if (info.getOverviewRulerPreferenceKey().equals(p)) {
				if (this.isAnnotationOverviewShown(info.getAnnotationType())) {
					this.showAnnotationOverview(info.getAnnotationType(), true);
				} else {
					this.hideAnnotationOverview(info.getAnnotationType(), true);
				}
				return;
			}
		}

	}

	/**
	 * Returns the shared color for the given key.
	 * 
	 * @param key
	 *            the color key string
	 * @return the shared color for the given key
	 */
	private Color getColor(String key) {
		if (this.fPreferenceStore != null) {
			final RGB rgb = PreferenceConverter.getColor(this.fPreferenceStore,
					key);
			return this.getColor(rgb);
		}
		return null;
	}

	/**
	 * Returns the shared color for the given RGB.
	 * 
	 * @param rgb
	 *            the RGB
	 * @return the shared color for the given RGB
	 */
	private Color getColor(RGB rgb) {
		return this.fSharedTextColors.getColor(rgb);
	}

	/**
	 * Returns the color of the given annotation type.
	 * 
	 * @param annotationType
	 *            the annotation type
	 * @return the color of the annotation type
	 */
	private Color getAnnotationTypeColor(Object annotationType) {
		final AnnotationPreference info = (AnnotationPreference) this.fAnnotationTypeKeyMap
				.get(annotationType);

		return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
	}

	/**
	 * Returns the layer of the given annotation type.
	 * 
	 * @param annotationType
	 *            the annotation type
	 * @return the layer
	 */
	private int getAnnotationTypeLayer(Object annotationType) {
		final AnnotationPreference info = (AnnotationPreference) this.fAnnotationTypeKeyMap
				.get(annotationType);
		if (info != null) {
			return info.getPresentationLayer();
		}
		return 0;
	}

	/**
	 * Enables showing of matching characters.
	 */
	private void showMatchingCharacters() {
		if (this.fMatchingCharacterPainter == null) {
			if (this.fSourceViewer instanceof ITextViewerExtension2) {
				this.fMatchingCharacterPainter = new MatchingCharacterPainter(
						this.fSourceViewer, this.fCharacterPairMatcher);
				this.fMatchingCharacterPainter.setColor(this
						.getColor(this.fMatchingCharacterPainterColorKey));
				final ITextViewerExtension2 extension = (ITextViewerExtension2) this.fSourceViewer;
				extension.addPainter(this.fMatchingCharacterPainter);
			}
		}
	}

	/**
	 * Disables showing of matching characters.
	 */
	private void hideMatchingCharacters() {
		if (this.fMatchingCharacterPainter != null) {
			if (this.fSourceViewer instanceof ITextViewerExtension2) {
				final ITextViewerExtension2 extension = (ITextViewerExtension2) this.fSourceViewer;
				extension.removePainter(this.fMatchingCharacterPainter);
				this.fMatchingCharacterPainter.deactivate(true);
				this.fMatchingCharacterPainter.dispose();
				this.fMatchingCharacterPainter = null;
			}
		}
	}

	/**
	 * Tells whether matching characters are shown.
	 * 
	 * @return <code>true</code> if the matching characters are shown
	 */
	private boolean areMatchingCharactersShown() {
		if ((this.fPreferenceStore != null)
				&& (this.fMatchingCharacterPainterEnableKey != null)) {
			return this.fPreferenceStore
					.getBoolean(this.fMatchingCharacterPainterEnableKey);
		}
		return false;
	}

	/**
	 * Shows the cursor line.
	 */
	private void showCursorLine() {
		if (this.fCursorLinePainter == null) {
			if (this.fSourceViewer instanceof ITextViewerExtension2) {
				this.fCursorLinePainter = new CursorLinePainter(
						this.fSourceViewer);
				this.fCursorLinePainter.setHighlightColor(this
						.getColor(this.fCursorLinePainterColorKey));
				final ITextViewerExtension2 extension = (ITextViewerExtension2) this.fSourceViewer;
				extension.addPainter(this.fCursorLinePainter);
			}
		}
	}

	/**
	 * Hides the cursor line.
	 */
	private void hideCursorLine() {
		if (this.fCursorLinePainter != null) {
			if (this.fSourceViewer instanceof ITextViewerExtension2) {
				final ITextViewerExtension2 extension = (ITextViewerExtension2) this.fSourceViewer;
				extension.removePainter(this.fCursorLinePainter);
				this.fCursorLinePainter.deactivate(true);
				this.fCursorLinePainter.dispose();
				this.fCursorLinePainter = null;
			}
		}
	}

	/**
	 * Tells whether the cursor line is shown.
	 * 
	 * @return <code>true</code> if the cursor line is shown
	 */
	private boolean isCursorLineShown() {
		if ((this.fPreferenceStore != null)
				&& (this.fCursorLinePainterEnableKey != null)) {
			return this.fPreferenceStore
					.getBoolean(this.fCursorLinePainterEnableKey);
		}
		return false;
	}

	/**
	 * Shows the margin.
	 */
	private void showMargin() {
		if (this.fMarginPainter == null) {
			if (this.fSourceViewer instanceof ITextViewerExtension2) {
				this.fMarginPainter = new MarginPainter(this.fSourceViewer);
				this.fMarginPainter.setMarginRulerColor(this
						.getColor(this.fMarginPainterColorKey));
				if (this.fPreferenceStore != null) {
					this.fMarginPainter
							.setMarginRulerColumn(this.fPreferenceStore
									.getInt(this.fMarginPainterColumnKey));
				}
				final ITextViewerExtension2 extension = (ITextViewerExtension2) this.fSourceViewer;
				extension.addPainter(this.fMarginPainter);

				this.fFontPropertyChangeListener = new FontPropertyChangeListener();
				JFaceResources.getFontRegistry().addListener(
						this.fFontPropertyChangeListener);
			}
		}
	}

	/**
	 * Hides the margin.
	 */
	private void hideMargin() {
		if (this.fMarginPainter != null) {
			if (this.fSourceViewer instanceof ITextViewerExtension2) {
				JFaceResources.getFontRegistry().removeListener(
						this.fFontPropertyChangeListener);
				this.fFontPropertyChangeListener = null;

				final ITextViewerExtension2 extension = (ITextViewerExtension2) this.fSourceViewer;
				extension.removePainter(this.fMarginPainter);
				this.fMarginPainter.deactivate(true);
				this.fMarginPainter.dispose();
				this.fMarginPainter = null;
			}
		}
	}

	/**
	 * Tells whether the margin is shown.
	 * 
	 * @return <code>true</code> if the margin is shown
	 */
	private boolean isMarginShown() {
		if ((this.fPreferenceStore != null)
				&& (this.fMarginPainterEnableKey != null)) {
			return this.fPreferenceStore
					.getBoolean(this.fMarginPainterEnableKey);
		}
		return false;
	}

	/**
	 * Enables annotations in the source viewer for the given annotation type.
	 * 
	 * @param annotationType
	 *            the annotation type
	 * @param updatePainter
	 *            if <code>true</code> update the annotation painter
	 * @since 3.0
	 */
	private void showAnnotations(Object annotationType, boolean updatePainter) {
		if (this.fSourceViewer instanceof ITextViewerExtension2) {
			if (this.fAnnotationPainter == null) {
				this.fAnnotationPainter = this.createAnnotationPainter();
				if (this.fSourceViewer instanceof ITextViewerExtension4) {
					((ITextViewerExtension4) this.fSourceViewer)
							.addTextPresentationListener(this.fAnnotationPainter);
				}
				final ITextViewerExtension2 extension = (ITextViewerExtension2) this.fSourceViewer;
				extension.addPainter(this.fAnnotationPainter);
			}
			this.fAnnotationPainter.setAnnotationTypeColor(annotationType, this
					.getAnnotationTypeColor(annotationType));
			final Object decorationType = this
					.getAnnotationDecorationType(annotationType);
			if (decorationType != null) {
				this.fAnnotationPainter.addAnnotationType(annotationType,
						decorationType);
			} else {
				this.fAnnotationPainter
						.addHighlightAnnotationType(annotationType);
			}

			if (updatePainter) {
				this.updateAnnotationPainter();
			}
		}
	}

	/**
	 * Creates and configures the annotation painter and configures.
	 * 
	 * @return an annotation painter
	 * @since 3.0
	 */
	protected AnnotationPainter createAnnotationPainter() {
		final AnnotationPainter painter = new AnnotationPainter(
				this.fSourceViewer, this.fAnnotationAccess);

		/*
		 * XXX: Could provide an extension point for drawing strategies, see:
		 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=51498
		 */
		painter.addDrawingStrategy(AnnotationPreference.STYLE_NONE,
				fgNullStrategy);
		painter.addDrawingStrategy(AnnotationPreference.STYLE_IBEAM,
				fgIBeamStrategy);

		{
			painter.addDrawingStrategy(AnnotationPreference.STYLE_BOX,
					new BoxDrawingStrategy());
			painter.addDrawingStrategy(AnnotationPreference.STYLE_DASHED_BOX,
					new DashedBoxDrawingStrategy());
			painter.addDrawingStrategy(AnnotationPreference.STYLE_SQUIGGLES,
					new AnnotationPainter.SquigglesStrategy());
			painter.addDrawingStrategy(AnnotationPreference.STYLE_UNDERLINE,
					new UnderlineDrawingStrategy());
		}

		return painter;
	}

	/**
	 * Updates the annotation painter.
	 * 
	 * @since 3.0
	 */
	private void updateAnnotationPainter() {
		if (this.fAnnotationPainter == null) {
			return;
		}

		this.fAnnotationPainter.paint(IPainter.CONFIGURATION);
		if (!this.fAnnotationPainter.isPaintingAnnotations()) {
			if (this.fSourceViewer instanceof ITextViewerExtension2) {
				final ITextViewerExtension2 extension = (ITextViewerExtension2) this.fSourceViewer;
				extension.removePainter(this.fAnnotationPainter);
			}
			if (this.fSourceViewer instanceof ITextViewerExtension4) {
				((ITextViewerExtension4) this.fSourceViewer)
						.removeTextPresentationListener(this.fAnnotationPainter);
			}

			this.fAnnotationPainter.deactivate(true);
			this.fAnnotationPainter.dispose();
			this.fAnnotationPainter = null;
		}
	}

	/**
	 * Hides annotations in the source viewer for the given annotation type.
	 * 
	 * @param annotationType
	 *            the annotation type
	 * @param updatePainter
	 *            if <code>true</code> update the annotation painter
	 * @since 3.0
	 */
	private void hideAnnotations(Object annotationType, boolean updatePainter) {
		if (this.fAnnotationPainter != null) {
			this.fAnnotationPainter.removeAnnotationType(annotationType);

			if (updatePainter) {
				this.updateAnnotationPainter();
			}
		}
	}

	/**
	 * Tells whether annotations are shown in the source viewer for the given
	 * type.
	 * 
	 * @param annotationType
	 *            the annotation type
	 * @return <code>true</code> if the annotations are shown
	 */
	private boolean areAnnotationsShown(Object annotationType) {
		if (ignoredAnnotationTypes.contains(annotationType))
			return false;
		if (this.fPreferenceStore != null) {	
			final AnnotationPreference info = (AnnotationPreference) this.fAnnotationTypeKeyMap
			.get(annotationType);
			if (info != null) {
				final String key = info.getTextPreferenceKey();					
					return (key != null) && this.fPreferenceStore.getBoolean(key);
				}			
		}
		return true;
	}

	/**
	 * Tells whether annotations are highlighted in the source viewer for the
	 * given type.
	 * 
	 * @param annotationType
	 *            the annotation type
	 * @return <code>true</code> if the annotations are highlighted
	 * @since 3.0
	 */
	private boolean areAnnotationsHighlighted(Object annotationType) {
		if (this.fPreferenceStore != null) {
			final AnnotationPreference info = (AnnotationPreference) this.fAnnotationTypeKeyMap
					.get(annotationType);
			if (info != null) {
				return (info.getHighlightPreferenceKey() != null)
						&& this.fPreferenceStore.getBoolean(info
								.getHighlightPreferenceKey());
			}
		}
		return false;
	}

	/**
	 * Tells whether annotation overview is enabled for the given type.
	 * 
	 * @param annotationType
	 *            the annotation type
	 * @return <code>true</code> if the annotation overview is shown
	 */
	private boolean isAnnotationOverviewShown(Object annotationType) {
		if ((this.fPreferenceStore != null) && (this.fOverviewRuler != null)) {
			final AnnotationPreference info = (AnnotationPreference) this.fAnnotationTypeKeyMap
					.get(annotationType);
			if (info != null) {
				return this.fPreferenceStore.getBoolean(info
						.getOverviewRulerPreferenceKey());
			}
		}
		return false;
	}

	/**
	 * Enable annotation overview for the given annotation type.
	 * 
	 * @param annotationType
	 *            the annotation type
	 * @param update
	 *            <code>true</code> if the overview should be updated
	 */
	private void showAnnotationOverview(Object annotationType, boolean update) {
		if (this.fOverviewRuler != null) {
			this.fOverviewRuler.setAnnotationTypeColor(annotationType, this
					.getAnnotationTypeColor(annotationType));
			this.fOverviewRuler.setAnnotationTypeLayer(annotationType, this
					.getAnnotationTypeLayer(annotationType));
			this.fOverviewRuler.addAnnotationType(annotationType);
			if (update) {
				this.fOverviewRuler.update();
			}
		}
	}

	/**
	 * Hides the annotation overview for the given type.
	 * 
	 * @param annotationType
	 *            the annotation type
	 * @param update
	 *            <code>true</code> if the overview should be updated
	 */
	private void hideAnnotationOverview(Object annotationType, boolean update) {
		if (this.fOverviewRuler != null) {
			this.fOverviewRuler.removeAnnotationType(annotationType);
			if (update) {
				this.fOverviewRuler.update();
			}
		}
	}

	/**
	 * Hides the annotation overview.
	 */
	public void hideAnnotationOverview() {
		if (this.fOverviewRuler != null) {
			final Iterator e = this.fAnnotationTypeKeyMap.keySet().iterator();
			while (e.hasNext()) {
				this.fOverviewRuler.removeAnnotationType(e.next());
			}
			this.fOverviewRuler.update();
		}
	}

	/**
	 * Sets the annotation overview color for the given annotation type.
	 * 
	 * @param annotationType
	 *            the annotation type
	 * @param color
	 *            the color
	 */
	private void setAnnotationOverviewColor(Object annotationType, Color color) {
		if (this.fOverviewRuler != null) {
			this.fOverviewRuler.setAnnotationTypeColor(annotationType, color);
			this.fOverviewRuler.update();
		}
	}
	
	public void addIgnoredAnnotationType(String annotationType)
	{
		ignoredAnnotationTypes.add(annotationType);
	}
	
	public void removeIgnoredAnnotationType(String annotationType)
	{
		ignoredAnnotationTypes.remove(annotationType);
	}
}
