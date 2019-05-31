package org.but4reuse.adapters.music.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

/**
 * 
 * @author Lydia Rodriguez-de la Nava
 *
 */

public class Sequence {

	public static Map<String, Integer> order = createMap();

	public static Map<String, Integer> createMap() {
		Map<String, Integer> myMap = new HashMap<String, Integer>();
		myMap.put("C", 1);
		myMap.put("D", 2);
		myMap.put("E", 3);
		myMap.put("F", 4);
		myMap.put("G", 5);
		myMap.put("A", 6);
		myMap.put("B", 7);
		return myMap;
	}

	/**
	 * If they don't have the same octave, returns the note with the highest
	 * octave. Else, compares the pitches.
	 * 
	 * @param n1
	 * @param n2
	 * @return The highest note between two note
	 */

	public static Note getHigherNote(Note n1, Note n2) {
		// System.out.println("Notes being compared : \n" + n1.getOctave() +
		// n1.getPitch() + "\n"
		// + n2.getOctave() + n2.getPitch());

		if (n2.getPitch().compareTo("Z") == 0 || n1.getPitch().compareTo("Z") == 0) {
			if (n1.getPitch().compareTo("Z") == 0 && n2.getPitch().compareTo("Z") != 0) {
				return n2;
			}
			if (n1.getPitch().compareTo("Z") != 0 && n2.getPitch().compareTo("Z") == 0) {
				return n1;
			}
			return n2;
		}
		if (n1.getOctave() != n2.getOctave()) {
			if (n1.getOctave() > n2.getOctave()) {
				return n1;
			}
			return n2;
		} else {
			if (order.get(n1.getPitch()) > order.get(n2.getPitch())) {
				return n1;
			}
			return n2;
		}
	}

	/**
	 * This function finds the monophonic contour of a sequence of notes. It
	 * returns the higher contour of the score. It doesn't take into account the
	 * duration of notes yet.
	 * 
	 * @param list
	 * @return
	 */

	public static List<Note> getContour(List<Note> list) {
		int nbmeas = 1;
		double start = 0.0;
		ArrayList<Note> res = new ArrayList<Note>();
		ArrayList<Note> tmp = new ArrayList<Note>();
		List<Note> copylist = new ArrayList<Note>(list); // copie pour éviter
															// une
															// ConcurrentModificationException

		// res.clear();
		Note lastnote = null;
		tmp.clear();
		// System.out.println("taille init " + tmp.size());

		// System.out.println("init terminée : prêt à entrer dans la boucle");

		for (Note note : copylist) {
			if (note.getMeasure() == nbmeas && note.getStartRelativeToMeasure() == start) {

				// System.out.println("added " + note + " to tmp");

				tmp.add(note);
				continue;
			} else {

				// System.out.println("cas où tmp.size>1");
				Note max = tmp.get(0);
				for (int i = 1; i < tmp.size(); i++) {
					max = getHigherNote(max, tmp.get(i));
				}
				res.add(max);

				// System.out.println("added " + note + " to res");
				// System.out.println("added " + max.toString() + "to res");

				// reinitialize for the next chord
				if (nbmeas != note.getMeasure()) {
					nbmeas++;
				}
				if (note.getStartRelativeToMeasure() != start) {
					start = note.getStartRelativeToMeasure();
				}
				tmp.clear();
				tmp.add(note);
			}

			// System.out.println("La liste à la fin de la boucle :\n" +
			// list.toString());

			lastnote = note;

		}

		// System.out.println(res);
		// System.out.println("last note of sequence " +lastnote);

		res.add(lastnote);

		// System.out.println(res);

		return res;
	}

	/**
	 * This function's goal is to ignore the notes in a sequence that are not
	 * important to the melody. It deletes the notes that are shorter than 4th
	 * notes.
	 * 
	 * @param sequence
	 * @param division
	 * @return
	 */
	public static List<Note> deleteShortNotes(List<Note> sequence, int division) {
		System.out.println("entering function");

		int duration = 0;
		List<Note> shortnotes = new ArrayList<Note>();
		List<Note> cleanseq = new ArrayList<Note>();

		for (Note note : sequence) {
			if (duration > 0) {

				// System.out.println("duration > 0");

				if (duration == division / 2) {

					// System.out.println("found une croche!");

					Note newnote = shortnotes.get(0);
					newnote.setDuration(duration);
					cleanseq.add(newnote);

					duration = 0;
					shortnotes.clear();
				}

				else if (note.getDuration() >= division / 2) {

					// System.out.println("next note is long, must add all short
					// notes");

					for (Note n : shortnotes) {
						cleanseq.add(n);
						duration = 0;
					}
				}
			}
			if (note.getDuration() >= division / 2) {
				cleanseq.add(note);
			}

			else {
				shortnotes.add(note);
				duration += note.getDuration();
			}
		}
		return cleanseq;
	}

	/**
	 * This functions slices up a sequence of notes into several smaller
	 * sequence of notes. It breaks the sequence if it find a note that is
	 * either the tonic or the dominant and that is long.
	 * 
	 * @param sequence
	 * @param fifths
	 * @param mode
	 * @param divisions
	 * @return
	 */

	public static List<List<Note>> sliceUp(List<Note> sequence, int fifths, String key, int divisions) {
		String tonic = PartUtils.getTonic(key);
		String dominant = PartUtils.getDominant(key, fifths);

		List<List<Note>> newlist = new ArrayList<List<Note>>();

		List<Note> list = new ArrayList<Note>();

		for (Note note : sequence) {
			list.add(note);
			if (endOfPhrase(note, tonic, dominant, divisions) && list.size() > 5) {
				// System.out.println("there is a phrase!!!!");

				// System.out.println(list);

				newlist.add(new ArrayList<Note>(list));

				list.clear();
			}
		}
		newlist.add(list);

		return newlist;

	}

	/**
	 * This function returns true if note is a long note, and is either the
	 * tonic or the dominant of a score.
	 * 
	 * @param note
	 * @param tonic
	 * @param dominant
	 * @param divisions
	 * @return
	 */

	public static boolean endOfPhrase(Note note, String tonic, String dominant, int divisions) {

		// System.out.println("the note is : " +
		// note.getPitch()+note.getAccidental());
		// System.out.println("comparing to tonic " + tonic + " and to dominant
		// " + dominant);

		String nameNote = note.getPitch() + note.getAccidental();

		if (nameNote.equals(tonic) || nameNote.equals(dominant) || note.getDuration() / divisions >= 2) {
			return true;
		}
		return false;
	}

	//////////////////// CHORDS //////////////////////////

	/**
	 * This function transforms a sequence of notes into a sequence of chords.
	 * Note that this function considers that every note that is played in the
	 * same measure at the same start relative to measure is a chord. That means
	 * that single notes will be considered as chords too, and you need to clean
	 * that with function cleanChordSequence.
	 * 
	 * @param list
	 * @return
	 */

	public static List<Chord> getChords(List<Note> list) {
		int nbmeas = 1;
		double start = 0.0;
		ArrayList<Chord> res = new ArrayList<Chord>();
		// ArrayList<Note> tmp = new ArrayList<Note>();

		Chord tmp = new Chord();
		List<Note> copylist = new ArrayList<Note>(list);
		for (Note note : copylist) {
			if (note.getPitch().equals("Z")) {
				continue;
			}

			if (note.getMeasure() == nbmeas && note.getStartRelativeToMeasure() == start) {
				// tmp.add(note);
				tmp.addNote(note);
				continue;
			} else {
				res.add(tmp);
				// reinitialize for the next chord
				if (nbmeas != note.getMeasure()) {
					nbmeas++;
				}
				if (note.getStartRelativeToMeasure() != start) {
					start = note.getStartRelativeToMeasure();
				}
				// tmp.clear();

				tmp = new Chord();
				tmp.addNote(note);
			}
		}

		return res;
	}

	/**
	 * Names each chord of a sequence.
	 * 
	 * @param list
	 * @return
	 */
	public static List<Chord> nameChords(List<Chord> list, String key) {
		for (Chord c : list) {
			c.findName();
			c.setDegree(key);
		}

		// create a new list that will contain the chord sequence without
		// duplication
		List<Chord> newlist = new ArrayList<Chord>();

		if (!list.isEmpty()) {
			newlist.add(list.get(0));

			for (int i = 1; i < list.size(); i++) {
				if (!list.get(i).getFullName().equals(list.get(i - 1).getFullName())) {
					newlist.add(list.get(i));
				}
			}
		}
		return newlist;
	}

	/**
	 * Gets rid of one note "chords". Note that a monophonic part will return an
	 * empty list.
	 * 
	 * @param list
	 */
	public static List<Chord> cleanChordSequence(List<Chord> list) {

		List<Chord> newlist = new ArrayList<Chord>();

		for (Chord chord : list) {
			chord.deleteDuplication();

			if (chord.size() > 1) {
				newlist.add(chord);
			}
		}

		// System.out.println("newlist " + newlist);

		return newlist;
	}

	/**
	 * This function slices a sequence of chords. A sequence if sliced if it
	 * ends by the 1st degree (the tonic), or if by a 5th (the dominant) not
	 * followed by a first.
	 * 
	 * @param seq
	 * @param key
	 * @param fifths
	 * @return
	 */
	public static List<List<Chord>> sliceUpChords(List<Chord> seq, String key, int fifths) {
		String tonic = PartUtils.getTonic(key);
		String dominant = PartUtils.getDominant(key, fifths);

		List<List<Chord>> newseq = new ArrayList<List<Chord>>();
		List<Chord> list = new ArrayList<Chord>();

		for (Chord chord : seq) {

			if (chord.getPitch() == tonic) {
				if (!list.isEmpty()) {
					newseq.add(new ArrayList<Chord>(list));
					newseq.clear();
				} else {
					list.add(chord);
				}
			} else {
				if (!list.isEmpty() && list.get(list.size() - 1).getPitch().equals(dominant)) {
					newseq.add(new ArrayList<Chord>(list));
					newseq.clear();
				}
				list.add(chord);
			}
		}
		return newseq;
	}

	/**
	 * Adds a note to a chord if it belongs to it.
	 * 
	 * @param c
	 * @param n
	 */
	private static void compareAndAdd(Chord c, Note n) {

		// System.out.println("entering compare and add");
		// System.out.println("comparing chord " + c + " and note " + n);

		boolean isThird = false, isFifth = false, addnote = false;

		for (Note note : c.getNotes()) {
			if (note.equals(n)) {
				return;
			}
			isThird = NoteUtils.isThird(note, n);
			isFifth = NoteUtils.isFifth(note, n);

			if ((c.size() == 2 || c.size() == 1) && (isThird || isFifth)) {
				addnote = true;
			} else if (c.size() == 3 && isThird) {
				addnote = true;
			}
		}
		if (addnote) {
			c.addNote(n);
		}

	}
}
