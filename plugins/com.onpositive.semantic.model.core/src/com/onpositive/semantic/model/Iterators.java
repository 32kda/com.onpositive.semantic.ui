package com.onpositive.semantic.model;





import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.onpositive.semantic.model.api.property.Function;
import com.onpositive.semantic.model.api.property.Predicate;

public class Iterators {
	
	
	
	 
	  public static <F, T> Collection<T> transform(Collection<F> fromCollection,
	      Function<? super F, T> function) {
	    return new TransformedCollection<F, T>(fromCollection, function);
	  }

	  static class TransformedCollection<F, T> extends AbstractCollection<T> {
	    final Collection<F> fromCollection;
	    final Function<? super F, ? extends T> function;

	    TransformedCollection(Collection<F> fromCollection,
	        Function<? super F, ? extends T> function) {
	      this.fromCollection = checkNotNull(fromCollection);
	      this.function = checkNotNull(function);
	    }

	    @Override public void clear() {
	      fromCollection.clear();
	    }

	    @Override public boolean isEmpty() {
	      return fromCollection.isEmpty();
	    }

	    @Override public Iterator<T> iterator() {
	      return Iterators.transform(fromCollection.iterator(), function);
	    }

	    @Override public int size() {
	      return fromCollection.size();
	    }
	  }
	
	  /**
	   * Returns the single element contained in {@code iterator}.
	   *
	   * @throws NoSuchElementException if the iterator is empty
	   * @throws IllegalArgumentException if the iterator contains multiple
	   *     elements.  The state of the iterator is unspecified.
	   */
	  public static <T> T getOnlyElement(Iterator<T> iterator) {
	    T first = iterator.next();
	    if (!iterator.hasNext()) {
	      return first;
	    }

	    StringBuilder sb = new StringBuilder();
	    sb.append("expected one element but was: <" + first);
	    for (int i = 0; i < 4 && iterator.hasNext(); i++) {
	      sb.append(", " + iterator.next());
	    }
	    if (iterator.hasNext()) {
	      sb.append(", ...");
	    }
	    sb.append('>');

	    throw new IllegalArgumentException(sb.toString());
	  }
	
	/**
	   * Creates a <i>mutable</i> {@code ArrayList} instance containing the given
	   * elements.
	   *
	   * <p><b>Note:</b> if mutability is not required and the elements are
	   * non-null, use {@link ImmutableList#copyOf(Iterator)} instead.
	   *
	   * @param elements the elements that the list should contain, in order
	   * @return a new {@code ArrayList} containing those elements
	   */
	  
	  public static <E> ArrayList<E> newArrayList(Iterator<? extends E> elements) {
	    checkNotNull(elements); // for GWT
	    ArrayList<E> list = new ArrayList<E>();
	    while (elements.hasNext()) {
	      list.add(elements.next());
	    }
	    return list;
	  }

	public static <A, B> Iterator<B> transform(final Iterator<A> it,
			@SuppressWarnings("rawtypes") final Function f) {
		return new Iterator<B>() {

			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			@SuppressWarnings("unchecked")
			@Override
			public B next() {
				return (B) f.apply(it.next());
			}

			@Override
			public void remove() {
				it.remove();
			}
		};
	}
	
	public static <T> Iterator<List<T>> partition(
		      Iterator<T> iterator, int size) {
		    return partitionImpl(iterator, size, false);
		}

	public static <T> Iterator<List<T>> paddedPartition(Iterator<T> iterator,
			int size) {
		return partitionImpl(iterator, size, true);
	}

	private static <T> Iterator<List<T>> partitionImpl(
			final Iterator<T> iterator, final int size, final boolean pad) {
		checkNotNull(iterator);
		
		return (Iterator<List<T>>) new Iterator<List<T>>() {
			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public List<T> next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				Object[] array = new Object[size];
				int count = 0;
				for (; count < size && iterator.hasNext(); count++) {
					array[count] = iterator.next();
				}
				for (int i = count; i < size; i++) {
					array[i] = null; // for GWT
				}

				@SuppressWarnings("unchecked")
				// we only put Ts in it
				List<T> list = Collections.unmodifiableList((List<T>) Arrays
						.asList(array));
				return (pad || count == size) ? list : list.subList(0, count);
			}

			@Override
			public void remove() {
				
			}
		};
	}

	/**
	 * Ensures that an object reference passed as a parameter to the calling
	 * method is not null.
	 * 
	 * @param reference
	 *            an object reference
	 * @return the non-null reference that was validated
	 * @throws NullPointerException
	 *             if {@code reference} is null
	 */
	public static <T> T checkNotNull(T reference) {
		if (reference == null) {
			throw new NullPointerException();
		}
		return reference;
	}

	static final Iterator<Object> EMPTY_ITERATOR = new Iterator<Object>() {
		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public Object next() {
			throw new NoSuchElementException();
		}

		@Override
		public void remove() {

		}
	};

	public static <T> Iterator<T> concat(
			final Iterator<? extends Iterator<? extends T>> inputs) {
		checkNotNull(inputs);
		return new Iterator<T>() {
			Iterator<? extends T> current = (Iterator<? extends T>) EMPTY_ITERATOR;
			Iterator<? extends T> removeFrom;

			@Override
			public boolean hasNext() {
				// http://code.google.com/p/google-collections/issues/detail?id=151
				// current.hasNext() might be relatively expensive, worth
				// minimizing.
				boolean currentHasNext;
				// checkNotNull eager for GWT
				// note: it must be here & not where 'current' is assigned,
				// because otherwise we'll have called inputs.next() before
				// throwing
				// the first NPE, and the next time around we'll call
				// inputs.next()
				// again, incorrectly moving beyond the error.
				while (!(currentHasNext = checkNotNull(current).hasNext())
						&& inputs.hasNext()) {
					current = inputs.next();
				}
				return currentHasNext;
			}

			@Override
			public T next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				removeFrom = current;
				return current.next();
			}

			@Override
			public void remove() {
				removeFrom.remove();
				removeFrom = null;
			}
		};
	}
	/**
	   * Returns {@code true} if one or more elements returned by {@code iterator}
	   * satisfy the given predicate.
	   */
	  public static <T> boolean any(
	      Iterator<T> iterator, Predicate<? super T> predicate) {
	    checkNotNull(predicate);
	    while (iterator.hasNext()) {
	      T element = iterator.next();
	      if (predicate.apply(element)) {
	        return true;
	      }
	    }
	    return false;
	  }

	public static <A> Iterator<A> filter(final Iterator<A> iterator,
			final Predicate predicate) {
		return new Iterator<A>() {

			A next;
			boolean nt;

			@Override
			public boolean hasNext() {
				if (!nt) {
					testNext();
				}
				return next != null;
			}

			@Override
			public A next() {
				A m = next;
				next = testNext();
				return m;
			}

			private A testNext() {
				while (iterator.hasNext()) {
					A next2 = iterator.next();
					if (predicate.apply(next2)) {
						return next2;
					}
				}
				return null;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public static int size(Iterator<?> z) {
		int a = 0;
		while (z.hasNext()) {
			z.next();
			a++;
		}
		return a;
	}
}
