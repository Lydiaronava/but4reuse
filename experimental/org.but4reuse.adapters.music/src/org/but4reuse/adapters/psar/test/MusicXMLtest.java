package org.but4reuse.adapters.psar.test;

import java.util.Collections;
import java.util.List;

import org.but4reuse.adapters.music.SequenceElement;
import org.but4reuse.adapters.music.utils.Chord;
import org.but4reuse.adapters.music.utils.Note;
import org.but4reuse.adapters.music.utils.NoteComparator;
import org.but4reuse.adapters.music.utils.PartUtils;
import org.but4reuse.adapters.music.utils.Sequence;
import org.but4reuse.adapters.music.utils.Similarity;
import org.but4reuse.adapters.music.utils.musicXMLparserDH;

public class MusicXMLtest {

	/*
	 * public static double similarity(List<Note> la, List<Note> lb) {
	 * 
	 * int m = la.size(); int n = lb.size();
	 * 
	 * 
	 * System.out.println("m = " + m + " n =" + n);
	 * 
	 * double distance[][] = new double[m+1][n+1];
	 * 
	 * 
	 * distance[0][0] = 0;
	 * 
	 * for(int i = 1; i < la.size()+1; i++) { distance[i][0] = distance[i-1][0]
	 * + Similarity.weightDeletion(la.get(i-1)); }
	 * 
	 * for(int j = 1; j < lb.size()+1; j++) { distance[0][j] = distance[0][j-1]
	 * + Similarity.weightInsertion(lb.get(j-1)); }
	 * 
	 * 
	 * int max_this = 0, min_element = 1000, min_this = 1000, max_element = 0;
	 * 
	 * for(Note note : la) { if(max_this < note.getDuration()) { max_this =
	 * note.getDuration(); } if(min_this > note.getDuration() &&
	 * note.getDuration() > 0) { min_this = note.getDuration(); } }
	 * 
	 * for(Note note : lb) { if(max_element < note.getDuration()) { max_element
	 * = note.getDuration(); } if(min_element > note.getDuration() &&
	 * note.getDuration() > 0) { min_element = note.getDuration(); } }
	 * 
	 * int F = (int) Math.ceil(max_this/min_element); int C = (int)
	 * Math.ceil(max_element/min_this);
	 * 
	 * 
	 * 
	 * // System.out.println("TAILLE DES ELEMENTS a = " + this.sequence.size() +
	 * " b = " + ((SequenceElement) anotherElement).sequence.size());
	 * 
	 * 
	 * 
	 * int i = 0, j = 0;
	 * 
	 * for(Note a : la) {
	 * 
	 * for(Note b : lb) {
	 * 
	 * 
	 * double deletion = distance[i][j+1] + Similarity.weightDeletion(a); double
	 * insertion = distance[i+1][j] + Similarity.weightInsertion(b); double
	 * fragmentation = Similarity.weightFragmentation( la, lb, i+1, j+1,
	 * F,distance); double consolidation = Similarity.weightConsolidation( la,
	 * lb, i+1, j+1, C,distance); double replacement = distance[i][j] +
	 * Similarity.weightReplacement(a, b);
	 * 
	 * distance[i+1][j+1] = Similarity.min(deletion, insertion, fragmentation,
	 * consolidation, replacement);
	 * 
	 * if(i==j) { System.out.println("I==J"+distance[i+1][j+1]); }
	 * 
	 * 
	 * j++;
	 * 
	 * 
	 * } i++; j = 0; }
	 * 
	 * 
	 * 
	 * for(int k = 0; k < m+1; k++) { for(int f = 0; f < n+1; f++) {
	 * System.out.print(Math.ceil(distance[k][f]) + "	"); }
	 * System.out.println(/*distance[k][k]); }
	 * 
	 * return (100-distance[m][n])/100; }
	 */

	public static void main(String args[]) {
		try {
			musicXMLparserDH parser = new musicXMLparserDH("/tmp/tests/similarity/sim1.xml");
			musicXMLparserDH parser2 = new musicXMLparserDH("/tmp/tests/similarity/sim2.xml");
			parser.parseMusicXML();
			parser2.parseMusicXML();
			List<Note> songSeq = parser.getNotesOfSong();
			List<Note> songSeq2 = parser2.getNotesOfSong();

			// System.out.println(songSeq);

			// met les notes qui se jouent au même moment à la suite les unes
			// des autres (quand il y a plusieurs notes)
			Collections.sort(songSeq, new NoteComparator());
			Collections.sort(songSeq2, new NoteComparator());

			List<Note> contour = Sequence.getContour(songSeq);
			List<Note> contour2 = Sequence.getContour(songSeq2);

			int measure = 1;
			for (Note n : contour) {
				if (measure != n.getMeasure()) {
					System.out.println("");
					measure++;
				}
				System.out.print(n.getDurationRelativeToMeasure());
			}

			// System.out.println(similarity(contour, contour2));

			/*
			 * List<Chord> chordseq = Sequence.getChords(songSeq); chordseq =
			 * Sequence.cleanChordSequence(chordseq);
			 * 
			 * for(Chord c : chordseq) { System.out.println(c.findName());
			 * //System.out.println(c.getVector()); }
			 * 
			 * System.out.println(chordseq);
			 * 
			 * for(Chord c : chordseq) { String key =
			 * PartUtils.getKeyOfPart(songSeq);
			 * 
			 * //System.out.println("KEYKEYKEY = " + key);
			 * 
			 * c.setDegree(key); System.out.println(c.getDegree()); }
			 * 
			 * 
			 * // System.out.println(PartUtils.getKeyOfPart(songSeq));
			 * 
			 * 
			 * /* int[] chord = {1,0,0,0,1,0,0,1,0,0,0,0};
			 * 
			 * int[] GM7 = {0,0,1,0,0,1,0,1,0,0,0,1};
			 * 
			 * int[] dim = {1,0,0,1,0,0,1,0,0,0,1,0};
			 * 
			 * int[] Ddim = {0,0,1,0,1,0,0,1,0,0,1,0};
			 * 
			 * System.out.println(Chord.findName(Ddim));
			 * 
			 * 
			 * 
			 * /* int fifths = parser.getFifths(); String key =
			 * PartUtils.getKeyOfPart(parser.getFifths(),
			 * songSeq.get(songSeq.size() - 1), parser.getMode()); int division
			 * = parser.getDivisions(); String mode = parser.getMode();
			 * 
			 * 
			 * List<List<Note>> seqnote = Sequence.getSequenceOfNotes(songSeq,
			 * division, fifths, mode);
			 * 
			 * 
			 * System.out.println(seqnote);
			 */

			// System.out.println(chords);

			/*
			
			 */

			/*
			 * int divisions = parser.getDivisions();
			 * 
			 * List<Note> contour = Sequence.getSequence(songSeq);
			 * 
			 * contour = Sequence.deleteShortNotes(contour, divisions);
			 * 
			 * if(contour.isEmpty()) { System.out.println(":("); }
			 * 
			 * for(Note note : contour) { System.out.println(note.getPitch() +
			 * " " + note.getDuration()); }
			 * 
			 * 
			 * 
			 */

			/*
			 * 
			 * int fifths = parser.getFifths(); //int divisions =
			 * parser.getDivisions(); String mode = parser.getMode();
			 * 
			 * System.out.println("fifth : " + fifths + "\nmode: " + mode +
			 * "\nnumber of divisions: " + divisions);
			 * 
			 * 
			 * String key = PartUtils.getKeyOfPart(parser.getFifths(),
			 * contour.get(contour.size() - 1), parser.getMode());
			 * System.out.println(key); String tonic = PartUtils.getTonic(key);
			 * String dominant = PartUtils.getDominant(key, parser.getFifths());
			 * System.out.println("tonique: " + tonic);
			 * System.out.println("dominante " + dominant);
			 * 
			 * List<List<Note>> elements = Sequence.sliceUp(contour, fifths,
			 * mode, divisions);
			 * 
			 * if(elements.isEmpty()) { System.out.println("BONJOUR TRISTESSE");
			 * } System.out.println(elements);
			 * 
			 */

			// List<Chord> chords = Sequence.getChordSequence(songSeq);
			// System.out.println(chords.size());
			// int i = 0;

			/*
			 * for(Chord chord : chords) { chord.deleteDuplication();
			 * System.out.println(++i + "eme accord\n :" + chord.toString());
			 * System.out.println(chord.findName()); }
			 * 
			 * 
			 * 
			 * //System.out.println(contour.size()); /*for(Note note : contour)
			 * { System.out.println(note.toString()); }
			 */
			/*
			 * 
			 * List<IElement> elements = new ArrayList<IElement>(); for (Note
			 * note : contour) { // ignore silence if
			 * (!note.getPitch().equals("Z")) { NoteElement noteElement = new
			 * NoteElement(note); elements.add(noteElement); } }
			 */
			// MusicXMLWriter.write(new File("/tmp/contour.xml"), "contour",
			// "Mozart", elements);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
