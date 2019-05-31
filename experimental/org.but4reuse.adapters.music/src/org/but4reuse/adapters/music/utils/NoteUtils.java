package org.but4reuse.adapters.music.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Lydia Rodriguez-de la Nava
 *
 */

public class NoteUtils {

	public static Map<String, Integer> hsteps = createMap();

	public static Map<String, Integer> createMap() {
		Map<String, Integer> myMap = new HashMap<String, Integer>();
		myMap.put("C", 0);
		myMap.put("C#", 1);
		myMap.put("Db", 1);
		myMap.put("D", 2);
		myMap.put("D#", 3);
		myMap.put("Eb", 3);
		myMap.put("E", 4);
		myMap.put("F", 5);
		myMap.put("F#", 6);
		myMap.put("Gb", 6);
		myMap.put("G", 7);
		myMap.put("G#", 8);
		myMap.put("Ab", 8);
		myMap.put("A", 9);
		myMap.put("A#", 10);
		myMap.put("Bb", 10);
		myMap.put("B", 11);
		return myMap;
	}

	/**
	 * Get the distance between two notes that is to say the number of
	 * half-steps
	 * 
	 * @param n1
	 * @param n2
	 * @return
	 */
	public static int distance(Note n1, Note n2) {
		// System.out.println("comparing " + n1.getPitch()+n1.getAccidental() +
		// " and " + n2.getPitch()+n2.getAccidental());

		if (n1.getPitch().equals("Z") || n2.getPitch().equals("Z")) {
			return -1;
		}

		int val1 = hsteps.get(n1.getPitch() + n1.getAccidental());
		int val2 = hsteps.get(n2.getPitch() + n2.getAccidental());
		if (val1 > val2) {
			return (12 - val1) + val2;
		}
		return val2 - val1;
	}

	public static int distance(String pitch1, String pitch2) {
		// System.out.println("comparing " + n1.getPitch()+n1.getAccidental() +
		// " and " + n2.getPitch()+n2.getAccidental());

		if (pitch1.equals("Z") || pitch2.equals("Z")) {
			return -1;
		}

		int val1 = hsteps.get(pitch1);
		int val2 = hsteps.get(pitch2);
		if (val1 > val2) {
			return (12 - val1) + val2;
		}
		return val2 - val1;
	}

	public static void fillExtraInfo(List<Note> song) {
		for (Note note : song) {
			note.setStartRelativeToMeasure(NoteUtils.getStartRelativeToMeasure(song, note));
			note.setDurationRelativeToMeasure(NoteUtils.getDurationRelativeToMeasure(song, note));
		}
	}

	/**
	 * Get the voices of a measure
	 * 
	 * @param song
	 * @param measureNumber
	 * @return
	 */
	public static List<Integer> getMeasureVoices(List<Note> measureNotes) {
		List<Integer> voices = new ArrayList<Integer>();
		for (Note n : measureNotes) {
			if (!voices.contains(n.getVoice())) {
				voices.add(n.getVoice());
			}
		}
		return voices;
	}

	/**
	 * Get the notes of a given measure and a given voice
	 * 
	 * @param song
	 * @param measureNumber
	 * @return
	 */
	public static List<Note> getMeasureNotesOfAGivenVoice(List<Note> song, int measureNumber, int voice) {
		List<Note> notes = new ArrayList<Note>();
		for (Note n : getMeasureNotes(song, measureNumber)) {
			if (n.getVoice().equals(voice)) {
				notes.add(n);
			}
		}
		return notes;
	}

	/**
	 * Get the notes of a given measure
	 * 
	 * @param song
	 * @param measureNumber
	 * @return
	 */
	public static List<Note> getMeasureNotes(List<Note> song, int measureNumber) {
		List<Note> notes = new ArrayList<Note>();
		for (Note n : song) {
			if (n.getMeasure().equals(measureNumber)) {
				notes.add(n);
			}
		}
		return notes;
	}

	public static double getMeasureDuration(List<Note> measureNotes) {
		// Get a voice
		int voice = getMeasureVoices(measureNotes).get(0);
		double totalDuration = 0;
		Note previousNote = null;
		for (Note n : measureNotes) {
			// Only consider one voice
			if (n.getVoice() == voice) {
				if (previousNote != null && previousNote.getStartTime().equals(n.getStartTime())) {
					// do not add
				} else {
					totalDuration += n.getDuration();
				}
				previousNote = n;
			}
		}
		return totalDuration;
	}

	public static double getDurationBeforeStart(List<Note> measureNotes, Note note) {
		// TODO consider liaison...
		double totalDuration = 0;
		Note previousNote = null;
		for (Note n : measureNotes) {
			if (note.getVoice() == n.getVoice()) {
				if (n.equals(note) || n.getStartTime().equals(note.getStartTime())) {
					return totalDuration;
				}
				if (previousNote != null && previousNote.getStartTime().equals(n.getStartTime())) {
					// do not add
				} else {
					totalDuration += n.getDuration();
				}
				previousNote = n;
			}
		}
		return totalDuration;
	}

	/**
	 * Get duration relative to measure
	 * 
	 * @param song
	 * @param note
	 * @return A value from 0 to 1
	 */
	public static double getDurationRelativeToMeasure(List<Note> song, Note note) {
		// TODO consider liaison...
		List<Note> measureNotes = getMeasureNotes(song, note.getMeasure());
		double totalDuration = getMeasureDuration(measureNotes);
		return note.getDuration() / totalDuration;
	}

	/**
	 * Get start relative to measure
	 * 
	 * @param song
	 * @param note
	 * @return A value from 0 to 1
	 */
	public static double getStartRelativeToMeasure(List<Note> song, Note note) {
		// TODO consider liaison...
		List<Note> measureNotes = getMeasureNotes(song, note.getMeasure());
		double totalDuration = getMeasureDuration(measureNotes);
		double beforeStartDuration = getDurationBeforeStart(measureNotes, note);
		return beforeStartDuration / totalDuration;
	}

	public static boolean isThird(Note n1, Note n2) {
		// System.out.println("distance entre " + n1+ " et " + n2);
		// System.out.println(distance(n1,n2) + " " + distance(n2, n1));
		return distance(n1, n2) == 3 || distance(n1, n2) == 4 || distance(n2, n1) == 3 || distance(n2, n1) == 4;
	}

	public static boolean isFifth(Note n1, Note n2) {
		// System.out.println("distance entre " + n1+ " et " + n2);
		// System.out.println("distance " + distance(n1,n2) + " " + distance(n2,
		// n1));
		return distance(n1, n2) == 7 || distance(n2, n1) == 7;
	}
}
