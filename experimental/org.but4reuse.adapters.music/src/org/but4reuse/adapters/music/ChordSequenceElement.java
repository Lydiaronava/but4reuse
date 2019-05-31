package org.but4reuse.adapters.music;

import java.util.ArrayList;
import java.util.List;

import org.but4reuse.adapters.IElement;
import org.but4reuse.adapters.impl.AbstractElement;
import org.but4reuse.adapters.music.utils.Chord;

/**
 * 
 * @author Lydia Rodriguez-de la Nava
 *
 */

public class ChordSequenceElement extends AbstractElement {

	public List<Chord> sequence;

	public ChordSequenceElement(List<Chord> elements) {
		this.sequence = elements;
	}

	/**
	 * The similarity between two chord sequences returns 1 if the sequences are
	 * exactly similar, that is if the degrees and the modes are the same.
	 */
	@Override
	public double similarity(IElement anotherElement) {
		if (anotherElement instanceof ChordSequenceElement) {
			ChordSequenceElement element = (ChordSequenceElement) anotherElement;

			if (element.sequence.size() == this.sequence.size()) {

				for (int i = 0; i < sequence.size(); i++) {
					if (!element.sequence.get(i).getDegree().equals(this.sequence.get(i).getDegree())
							&& !element.sequence.get(i).getMode().equals(this.sequence.get(i).getMode())) {
						return 0;
					}
				}

			} else {
				return 0;
			}
		}

		return 1;
	}

	@Override
	public String getText() {
		String text = " CHORDS :";
		for (Chord chord : sequence) {
			text += " " + chord.getFullName() + " " + chord.getDegree();
		}
		return text;
	}

	@Override
	public ArrayList<String> getWords() {
		ArrayList<String> words = new ArrayList<String>();
		String text = "[";

		for (Chord chord : sequence) {
			text += chord.getDegree() + " " + chord.findName();
		}
		words.add(text);

		return words;
	}

}
