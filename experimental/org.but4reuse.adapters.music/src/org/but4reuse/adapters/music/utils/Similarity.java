package org.but4reuse.adapters.music.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * These are the methods that implement Mongeau-Sankoff operation for note
 * sequence similarity.
 * 
 * @author Lydia Rodriguez-de la Nava
 *
 */

public class Similarity {

	private static double[] ton = { 0.6, 2.6, 2.3, 1.0, 1.0, 1.6, 1.8, 0.8, 1.3, 1.3, 2.2, 2.5 };

	private static double rest = 0.1;

	public static double k1 = 0.348;

	public static double weightInsertion(Note n) {
		return k1 * n.getDuration();
	}

	public static double weightDeletion(Note n) {
		return k1 * n.getDuration();
	}

	public static double weightReplacement(Note a, Note b) {
		return k1 * wlength(a, b) + winterval(a, b);
	}

	public static double weightFragmentation(List<Note> a, List<Note> b, int i, int j, int F, double distance[][]) {
		int min = Math.min(j, F);
		double res = 100000;
		double interval = 0.0;
		int length = 0;

		for (int k = 2; k < min; k++) {
			int tmp = k;

			interval = distance[i - 1][j - tmp];
			length = 0;

			while (tmp > 0) {
				interval += winterval(a.get(i - 1), b.get(j - tmp));
				length += b.get(j - tmp).getDuration();
				tmp--;
			}
			double weight = interval + k1 * wlength(length, a.get(i - 1).getDuration());
			res = Math.min(weight, res);
		}
		return res;
	}

	public static double weightConsolidation(List<Note> a, List<Note> b, int i, int j, int C, double distance[][]) {
		int min = Math.min(i, C);
		double res = 1000000;
		double interval = 0;
		int length = 0;

		for (int k = 2; k < min; k++) {
			int tmp = k;

			interval = distance[i - tmp][j - 1];

			while (tmp > 0) {
				interval += winterval(a.get(i - tmp), b.get(j - 1));
				length += a.get(i - tmp).getDuration();
				tmp--;
			}
			double weight = interval + k1 * wlength(length, b.get(j - 1).getDuration());
			res = Math.min(res, weight);
		}
		return res;
	}

	/**
	 * the difference between the two notes
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static int wlength(Note a, Note b) {
		return Math.abs(a.getDuration() - b.getDuration());
	}

	public static int wlength(int a, int b) {
		return Math.abs(a - b);
	}

	/**
	 * predefined value determined by the relative position of the notes a and b
	 * * length of the shorter of a and b
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static double winterval(Note a, Note b) {
		double wint;

		if (a.getPitch().equals("Z") || b.getPitch().equals("Z")) {
			if (a.getPitch().equals("Z") && b.getPitch().equals("Z")) {
				wint = ton[0];
			} else
				wint = rest;
		} else {
			wint = NoteUtils.distance(a, b);
		}

		return wint * Math.min(a.getDuration(), b.getDuration());
	}

	public static double min(double a, double b, double c, double d, double e) {
		return Math.min(a, Math.min(b, Math.min(c, Math.min(d, e))));
	}

}
