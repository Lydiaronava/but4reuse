package org.but4reuse.adapters.music;

import java.io.File;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.but4reuse.adaptedmodel.helpers.AdaptedModelHelper;
import org.but4reuse.adaptedmodel.manager.AdaptedModelManager;
import org.but4reuse.adapters.IAdapter;
import org.but4reuse.adapters.IElement;
import org.but4reuse.adapters.music.utils.Chord;
import org.but4reuse.adapters.music.utils.MusicXMLWriter;
import org.but4reuse.adapters.music.utils.Note;
import org.but4reuse.adapters.music.utils.NoteComparator;
import org.but4reuse.adapters.music.utils.NoteUtils;
import org.but4reuse.adapters.music.utils.PartUtils;
import org.but4reuse.adapters.music.utils.Sequence;
import org.but4reuse.adapters.music.utils.musicXMLparserDH;
import org.but4reuse.utils.files.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Music Adapter
 * 
 * @author jabier.martinez & Lydia Rodriguez-de la Nava
 */
public class MusicAdapter implements IAdapter {

	@Override
	/**
	 * is it a musicxml file
	 */
	public boolean isAdaptable(URI uri, IProgressMonitor monitor) {
		// xml is a very common extension, lets return false for the moment and
		// put the responsibility on user selection
		return false;
	}

	@Override
	public List<IElement> adapt(URI uri, IProgressMonitor monitor) {
		List<IElement> elements = new ArrayList<IElement>();

		try {
			File file = FileUtils.getFile(uri);
			musicXMLparserDH parser = new musicXMLparserDH(file.getAbsolutePath());
			parser.parseMusicXML();

			// get key alterations
			int fifths = parser.getFifths();

			// get rhythm related info
			int division = parser.getDivisions();

			List<Note> songSequenceOfNoteObjects = parser.getNotesOfSong();
			Collections.sort(songSequenceOfNoteObjects, new NoteComparator());
			NoteUtils.fillExtraInfo(songSequenceOfNoteObjects);

			// find the key of the part
			String key = PartUtils.getKeyOfPart(songSequenceOfNoteObjects);

			// SEQUENCE ELEMENT

			// find the high contour
			List<Note> contour = Sequence.getContour(songSequenceOfNoteObjects);

			// delete unimportant notes
			contour = Sequence.deleteShortNotes(contour, division);

			// slice the contour into smaller sections
			List<List<Note>> seqEl = Sequence.sliceUp(contour, fifths, key, division);

			for (List<Note> section : seqEl) {

				System.out.println("ELEMENT = " + section);

				SequenceElement se = new SequenceElement(section);
				se.addDependency(new KeyDependencyObject(key));
				elements.add(se);
			}

			// CHORD SEQUENCE ELEMENT

			// get all the chords
			List<Chord> chords = Sequence.getChords(songSequenceOfNoteObjects);

			// delete one note chords
			chords = Sequence.cleanChordSequence(chords);

			// if the score is monophonic, there won't be any chord elements

			// name all the chords
			chords = Sequence.nameChords(chords, key);

			// slice the chord sequence into smaller sections
			List<List<Chord>> chordSeq = Sequence.sliceUpChords(chords, key, fifths);

			for (List<Chord> section : chordSeq) {
				System.out.println(section.toString());
				
				ChordSequenceElement cse = new ChordSequenceElement(section);

				cse.addDependency(new KeyDependencyObject(key));

				elements.add(cse);

			}

			for (Note note : songSequenceOfNoteObjects) {
				// ignore silence
				if (!note.getPitch().equals("Z")) {
					NoteElement noteElement = new NoteElement(note, key);

					noteElement.addDependency(new KeyDependencyObject(key));

					elements.add(noteElement);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("ALL THE ELEMENTS : " + elements);
		return elements;
	}

	@Override
	public void construct(URI uri, List<IElement> elements, IProgressMonitor monitor) {

		System.out.println("entered construct");
		try {
			// Use the given file or use a default name if a folder was given
			if (uri.toString().endsWith("/")) {
				uri = new URI(uri.toString() + "score.xml");
			}
			// Create file if it does not exist
			File file = FileUtils.getFile(uri);
			FileUtils.createFile(file);
			String name = AdaptedModelHelper.getName(AdaptedModelManager.getAdaptedModel());
			if (name == null) {
				name = "";
			}
			String author = "Generated " + System.currentTimeMillis();
			MusicXMLWriter.write(file, name, author, elements);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
