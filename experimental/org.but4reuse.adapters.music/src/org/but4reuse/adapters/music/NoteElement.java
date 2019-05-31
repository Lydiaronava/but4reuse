package org.but4reuse.adapters.music;

import java.util.ArrayList;

import org.but4reuse.adapters.IElement;
import org.but4reuse.adapters.impl.AbstractElement;
import org.but4reuse.adapters.music.utils.Note;
import org.but4reuse.adapters.music.utils.NoteUtils;

/**
 * Note Element
 * 
 * @author jabier.martinez
 */
public class NoteElement extends AbstractElement {

	public Note note;
	public String key;
	public int distance;

	public NoteElement(Note note) {
		super();
		this.note = note;
	}

	public NoteElement(Note note, String key) {
		super();
		this.note = note;
		this.key = key;

		if (key.contains("M") || key.contains("m")) {
			key = key.substring(0, key.length() - 1);
		}

		distance = NoteUtils.distance(note.getPitch() + note.getAccidental(), key);
	}

	@Override
	public double similarity(IElement anotherElement) {
		if (anotherElement instanceof NoteElement) {
			NoteElement another = (NoteElement) anotherElement;
			// One is silence and not the other
			if (another.note.isRest() && !note.isRest()) {
				return 0;
			}
			if (!another.note.isRest() && note.isRest()) {
				return 0;
			}
			if (another.note.getPart().equals(note.getPart()) && another.note.getMeasure().equals(note.getMeasure())) {
				if (another.note.getStartRelativeToMeasure() == note.getStartRelativeToMeasure()
						&& another.note.getDurationRelativeToMeasure() == note.getDurationRelativeToMeasure()
						/* && another.note.getPitch().equals(note.getPitch()) */
						&& distance == another.distance
						/*
						 * && another.note.getAccidental().equals(note.
						 * getAccidental())
						 */
						&& another.note.isGrace() == note.isGrace()) {
					if (another.note.isRest()) {
						// This is enough for two silences
						return 1;
					} else {
						// Check for two notes
						if (another.note.getOctave().equals(note.getOctave())
								&& another.note.getType().equals(note.getType())) {
							return 1;
						} else if (another.note.getType().equals(note.getType())) {
							// just different octave. Octave similarity
							return 0.9;
						}
					}
				}
			}
		}
		return 0;
	}

	@Override
	public String getText() {
		return note.toString();
	}

	@Override
	public ArrayList<String> getWords() {
		
		ArrayList<String> list = new ArrayList<String>();
		
		list.add(note.getPitch()+note.getAccidental());
		
		return list;
	}

}
