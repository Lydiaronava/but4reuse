package org.but4reuse.adapters.music.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.text.html.HTMLDocument.Iterator;

/**
 * 
 * @author Lydia Rodriguez-de la Nava
 *
 */

public class Chord {
	private List<Note> notes = new ArrayList<Note>();
	private String pitch = "";
	private String accidental = "";
	private String mode = "";
	private String degree = "";
	private int[] vector = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	private static int chordtypes = 13;

	/**
	 * Chord Type Chosen
	 */
	private static int[][] ctp = { { 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0 }, { 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1 },
			{ 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1 }, { 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0 },
			{ 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0 }, { 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1 },
			{ 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1 }, { 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0 },
			{ 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0 }, { 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0 },
			{ 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0 }, { 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0 },
			{ 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0 }, };

	private static String[] chordModes = { "m", "m7", "mM7", "7", "M", "M7", "aug", "sus4", "sus2", "5", "dim", "dim7",
			"m7-5", };

	public static Map<Integer, String> hsteps = createMap();

	public static Map<Integer, String> createMap() {
		Map<Integer, String> myMap = new HashMap<Integer, String>();
		myMap.put(0, "C");
		myMap.put(1, "C#");
		myMap.put(1, "Db");
		myMap.put(2, "D");
		myMap.put(3, "D#");
		myMap.put(3, "Eb");
		myMap.put(4, "E");
		myMap.put(5, "F");
		myMap.put(6, "F#");
		myMap.put(6, "Gb");
		myMap.put(7, "G");
		myMap.put(8, "G#");
		myMap.put(8, "Ab");
		myMap.put(9, "A");
		myMap.put(10, "A#");
		myMap.put(10, "Bb");
		myMap.put(11, "B");
		return myMap;
	}

	/*
	 * Deletes repeated notes in a chord for it to be minimal
	 */
	public void deleteDuplication() {
		List<Note> newnotes = new ArrayList<Note>();

		Map<String, Integer> map = new HashMap<String, Integer>();

		for (Note note : notes) {
			if (!map.containsKey(note.getPitch() + note.getAccidental())) {
				map.put(note.getPitch() + note.getAccidental(), 0);
				newnotes.add(new Note(note.getPitch(), note.getAccidental()));
			}
		}

		notes = newnotes;

	}

	/**
	 * Takes the list of notes and finds its corresponding name in the form of
	 * pitch + mode. The chord must be without duplications.
	 * 
	 * @return name of the chord
	 */

	public String findName() {
		setVector();

		int[] pcp = vector;

		int minscore = 12;
		int tmpscore = 0;

		int index = -1;
		int root = -1;

		for (int k = 0; k < chordtypes; k++) {

			// System.out.println("testing " + chordModes[k]);

			int[] T = ctp[k];

			for (int i = 0; i < 12; i++) {

				// System.out.println("I ++");

				for (int j = 0; j < 12; j++) {

					// System.out.println("comparing " + j + " and " +
					// (i+j)%12);

					int calc = T[(i + j) % 12] - pcp[j];

					// System.out.println("calc = " + calc);

					tmpscore += Math.pow(calc, 2);

				}

				// System.out.println("score = " + tmpscore);

				if (minscore > tmpscore) {
					minscore = tmpscore;
					index = k;
					root = i;

					// System.out.println("new score = " + minscore);
					// System.out.println("root =" + i);
				}
				tmpscore = 0;
			}

		}

		if (root < 0 && index < 0) {
			return "Z";
		}

		pitch = hsteps.get((12 - root) % 12);
		mode = chordModes[index];

		return hsteps.get((12 - root) % 12) + chordModes[index];

	}

	public void setVector() {
		for (Note note : notes) {
			this.vector[NoteUtils.hsteps.get(note.getPitch() + note.getAccidental())] = 1;
		}
	}

	public int[] getVector() {
		return vector;
	}

	public void addChord(Chord c) {
		for (Note note : c.getNotes()) {
			this.addNote(note);
		}
	}

	public List<Note> getNotes() {
		return notes;
	}

	public void setNotes(List<Note> notes) {
		this.notes = notes;
	}

	public void addNote(Note n) {
		notes.add(n);
	}

	public void clear() {
		notes.clear();
	}

	public String getPitch() {
		return pitch;
	}

	public void setPitch(String pitch) {
		this.pitch = pitch;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public void setDegree(String key) {

		String tonic = key.subSequence(0, key.length() - 1).toString();

		System.out.println("tonic " + tonic + " chord = " + pitch);

		int distance = Math.abs(NoteUtils.hsteps.get(tonic) - NoteUtils.hsteps.get(pitch));

		if (key.contains("m")) {

			switch (distance) {
			case 0:
				degree = "I";
				break;
			case 2:
				degree = "II";
				break;
			case 3:
				degree = "III";
				break;
			case 5:
				degree = "IV";
				break;
			case 7:
				degree = "V";
				break;
			case 8:
				degree = "VI";
				break;
			case 10:
				degree = "VII";
				break;
			}
		}

		else {
			switch (distance) {
			case 0:
				degree = "I";
				break;
			case 2:
				degree = "II";
				break;
			case 4:
				degree = "III";
				break;
			case 5:
				degree = "IV";
				break;
			case 7:
				degree = "V";
				break;
			case 9:
				degree = "VI";
				break;
			case 11:
				degree = "VII";
				break;
			}
		}

	}

	public String getDegree() {
		return degree;
	}

	public String getFullName() {
		return pitch + accidental + mode;
	}

	public String toString() {
		return notes.toString() + mode + pitch;
	}

	public int size() {
		return notes.size();
	}

	public boolean equals(Chord c) {
		return c.getDegree().equals(this.getDegree());
	}

	/*
	 * public String findName() { int tierce = 0, quinte = 0, septieme = 0;
	 * boolean success = false; for(Note n1 : notes) {
	 * 
	 * tierce = 0; quinte = 0; septieme = 0; boolean boolt = false, boolq =
	 * false, bools = false;
	 * 
	 * for(Note n2 : notes) { int res = NoteUtils.distance(n1, n2); if(res == 3
	 * || res == 4) { tierce = res; boolt = true; } if(res == 7) { quinte = res;
	 * boolq = true; } if(res == 11) { septieme = res; bools = true; } }
	 * if(boolt && boolq) { success = true; this.pitch = n1.getPitch();
	 * this.accidental = n1.getAccidental(); break; } } if(!success) {
	 * this.pitch = "unknown"; return
	 * "impossible to identify the name of the chord"; }
	 * 
	 * if(tierce == 4 && quinte == 7) { this.mode = "M"; } if(tierce == 3 &&
	 * quinte == 7) { this.mode = "m"; } if(septieme == 11) { this.mode += "7";
	 * }
	 * 
	 * return pitch+mode; }
	 */
}
