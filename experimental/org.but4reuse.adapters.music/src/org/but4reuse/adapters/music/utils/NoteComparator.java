package org.but4reuse.adapters.music.utils;

import java.util.Comparator;

/**
 * 
 * @author Lydia Rodriguez-de la Nava
 *
 */

public class NoteComparator implements Comparator<Note> {

	/**
	 * -1 if o1 < o2 0 if o1 == o2 1 if o1 > o2
	 * 
	 */
	@Override
	public int compare(Note o1, Note o2) {
		if (o1.getMeasure() == o2.getMeasure() && o1.getStartRelativeToMeasure() == o2.getStartRelativeToMeasure()) {
			return 0;
		}
		if (o1.getMeasure() < o2.getMeasure()) {
			return -1;
		}
		if (o2.getMeasure() > o2.getMeasure()) {
			return 1;
		}
		if (o1.getMeasure() == o2.getMeasure()) {
			if (o1.getStartRelativeToMeasure() < o2.getStartRelativeToMeasure()) {
				return -1;
			} else {
				return 1;
			}
		}
		return 0;
	}

}
