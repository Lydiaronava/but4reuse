package org.but4reuse.adapters.psar.test;

import org.but4reuse.adapters.music.utils.Note;
import org.but4reuse.adapters.music.utils.NoteUtils;

public class test {

	public static void main(String args[]) {
		Note n = new Note("F");
		Note n1 = new Note("D");
		System.out.println(NoteUtils.distance(n, n1));
	}
}
