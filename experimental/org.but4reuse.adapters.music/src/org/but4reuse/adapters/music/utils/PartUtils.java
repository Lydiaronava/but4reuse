package org.but4reuse.adapters.music.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @author Lydia Rodriguez-de la Nava
 *
 */

public class PartUtils {

	public static Map<Integer, String> keys = createMap();

	/**
	 * The keys linked with their number of fifths.
	 * 
	 * @return
	 */
	public static Map<Integer, String> createMap() {
		Map<Integer, String> myMap = new HashMap<Integer, String>();
		myMap.put(0, "C");
		myMap.put(1, "G");
		myMap.put(2, "D");
		myMap.put(3, "A");
		myMap.put(4, "E");
		myMap.put(5, "B");
		myMap.put(6, "F#");
		myMap.put(7, "C#");
		myMap.put(-1, "F");
		myMap.put(-2, "Bb");
		myMap.put(-3, "Eb");
		myMap.put(-4, "Ab");
		myMap.put(-5, "Db");
		myMap.put(-6, "Gb");
		myMap.put(-7, "Cb");
		return myMap;
	}

	/**
	 * Data for key finding algorithm
	 */
	public static double[] major_profile = { 6.35, 2.23, 3.48, 2.33, 4.38, 4.09, 2.52, 5.19, 2.39, 3.66, 2.29, 2.88

	};

	/**
	 * Data for key finding algorithm
	 */
	public static double[] minor_profile = { 6.33, 2.68, 3.52, 5.38, 2.6, 3.53, 2.54, 4.75, 3.98, 2.69, 3.34, 3.17 };

	/**
	 * This function applies the Krumhansl-Schmuckler algorithm in order to find
	 * the key of a part.
	 * 
	 * @param list
	 * @return
	 */
	public static String getKeyOfPart(List<Note> list) {

		String mode = "";
		int pos = 0;

		double[] duration = new double[12];
		for (int i = 0; i < 12; i++) {
			duration[i] = 0;
		}

		for (Note note : list) {
			if (note.getPitch() != "Z") {
				int index = NoteUtils.hsteps.get(note.getPitch() + note.getAccidental());
				duration[index] += note.getDurationRelativeToMeasure();
			}
		}

		double major_x_avg = 0, minor_x_avg = 0, y_avg = 0;

		for (int i = 0; i < 12; i++) {
			major_x_avg += major_profile[i];
			minor_x_avg += minor_profile[i];
			y_avg += duration[i];
		}

		major_x_avg = major_x_avg / 12;
		minor_x_avg = minor_x_avg / 12;
		y_avg = y_avg / 12;

		double maj_nom = 0, maj_denom_l = 0, maj_denom_r = 0;
		double min_nom = 0, min_denom_l = 0, min_denom_r = 0;

		double max_r = 0.0, minor_tmp_r = 0.0, major_tmp_r = 0.0;

		for (int i = 0; i < 12; i++) {
			for (int j = 0; j < 12; j++) {
				maj_nom += (major_profile[(i + j) % 12] - major_x_avg) * (duration[j] - y_avg);
				min_nom += (minor_profile[(i + j) % 12] - minor_x_avg) * (duration[j] - y_avg);

				maj_denom_l += (major_profile[(i + j) % 12] - major_x_avg)
						* (major_profile[(i + j) % 12] - major_x_avg);
				maj_denom_r += (duration[j] - y_avg) * (duration[j] - y_avg);

				min_denom_l += (minor_profile[(i + j) % 12] - minor_x_avg)
						* (minor_profile[(i + j) % 12] - minor_x_avg);
				min_denom_r += (duration[j] - y_avg) * (duration[j] - y_avg);
			}
			minor_tmp_r = min_nom / Math.sqrt(min_denom_l * min_denom_r);
			major_tmp_r = maj_nom / Math.sqrt(maj_denom_l * maj_denom_r);

			if (minor_tmp_r > max_r && minor_tmp_r > major_tmp_r) {
				mode = "m";
				pos = i;
			}
			if (major_tmp_r > max_r && major_tmp_r > minor_tmp_r) {
				mode = "M";
				pos = i;
			}

			max_r = Math.max(minor_tmp_r, Math.max(major_tmp_r, max_r));

		}

		return Chord.hsteps.get(pos) + mode;
	}

	/**
	 * Chooses between twho homophonic notes with respect to the number of
	 * fifths
	 * 
	 * @param fifths
	 * @param list
	 * @return
	 */
	static String chooseNote(int fifths, List<String> list) {
		if (list.size() == 1) {
			return list.get(0);
		}
		for (String note : list) {
			if (fifths > 0 && note.contains("#")) {
				return note;
			}
			if (fifths < 0 && note.contains("b")) {
				return note;
			}
		}
		return null;
	}

	/**
	 * Gets the name of the note(s) that correspond to the value.
	 * 
	 * @param value
	 * @return
	 */
	private static List<String> getPossibleRelatives(int value) {
		List<String> possibleValues = new ArrayList<String>();

		// System.out.println("get possible relatives " + possibleValues);

		int newvalue = value - 3;
		if (newvalue < 0) {
			newvalue = 12 + newvalue;
		}
		// System.out.println("value of relative " + newvalue);

		for (Entry<String, Integer> note : NoteUtils.hsteps.entrySet()) {
			if (note.getValue() == (newvalue)) {
				possibleValues.add(note.getKey());
			}
		}

		// System.out.println("get possible relatives " + possibleValues);

		return possibleValues;
	}

	/**
	 * This function gets the dominant of the key.
	 * 
	 * @param songkey
	 * @param fifths
	 * @return
	 */
	public static String getDominant(String songkey, int fifths) {
		List<String> possibleValues = new ArrayList<String>();
		String key = getTonic(songkey);
		int value = NoteUtils.hsteps.get(key);
		value = (value + 7) % 12;

		for (Entry<String, Integer> note : NoteUtils.hsteps.entrySet()) {
			if (note.getValue() == (value)) {
				possibleValues.add(note.getKey());
			}
		}
		return chooseNote(fifths, possibleValues);
	}

	/**
	 * This function gets the tonic of the key.
	 * 
	 * @param key
	 * @return
	 */
	public static String getTonic(String key) {
		if (key.contains("M")) {
			return key.replace("M", "");
		}
		return key.replace("m", "");
	}

	private static String getMinorRelative(String key, int fifths) {
		int value = NoteUtils.hsteps.get(key);

		// System.out.println("value of possible key " + value);

		List<String> relatives = getPossibleRelatives(value);

		// System.out.println("possible relatives are " + relatives);

		return chooseNote(fifths, relatives);
	}

}

/*
 * public static String getKeyOfPart(int fifths, Note lastNote, String mode) {
 * String key = keys.get(fifths);
 * 
 * if(mode == "minor") {
 * 
 * //System.out.println("mode == minor");
 * 
 * return getMinorRelative(key,fifths) + "m"; }
 * 
 * //System.out.println(key + " ==  " + lastNote.getPitch() +
 * lastNote.getAccidental() + "?");
 * 
 * if((lastNote.getPitch() + lastNote.getAccidental()).equals(key)) { return key
 * + "M"; } else {
 * 
 * //System.out.println("must search relative minor key");
 * 
 * return getMinorRelative(key, fifths) + "m"; } }
 */
