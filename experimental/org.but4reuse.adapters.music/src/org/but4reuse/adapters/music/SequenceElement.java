package org.but4reuse.adapters.music;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.but4reuse.adapters.IElement;
import org.but4reuse.adapters.impl.AbstractElement;
import org.but4reuse.adapters.music.utils.Note;
import org.but4reuse.adapters.music.utils.PartUtils;
import org.but4reuse.adapters.music.utils.Similarity;

import com.sun.javafx.scene.traversal.WeightedClosestCorner;

/**
 * 
 * @author Lydia Rodriguez-de la Nava
 *
 */

public class SequenceElement extends AbstractElement {

	public List<Note> sequence;

	public SequenceElement(List<Note> sequence) {
		this.sequence = sequence;
	}

	/**
	 * The similarity between note sequences is the Mongeau & Sankoff algorithm
	 */
	@Override
	public double similarity(IElement anotherElement) {

		if (anotherElement instanceof SequenceElement) {

			SequenceElement element = (SequenceElement) anotherElement;

			if (!element.sequence.isEmpty() && !this.sequence.isEmpty()) {

				int m = this.getSize();
				int n = element.getSize();

				double distance[][] = new double[m + 1][n + 1];

				distance[0][0] = 0;

				for (int i = 1; i < this.getSize() + 1; i++) {
					distance[i][0] = distance[i - 1][0] + Similarity.weightDeletion(this.sequence.get(i - 1));
				}

				for (int j = 1; j < element.getSize() + 1; j++) {
					distance[0][j] = distance[0][j - 1] + Similarity.weightInsertion(element.sequence.get(j - 1));
				}

				int max_this = 0, min_element = 1000, min_this = 1000, max_element = 0;

				for (Note note : this.sequence) {
					if (max_this < note.getDuration()) {
						max_this = note.getDuration();
					}
					if (min_this > note.getDuration() && note.getDuration() > 0) {
						min_this = note.getDuration();
					}
				}

				for (Note note : element.sequence) {
					if (max_element < note.getDuration()) {
						max_element = note.getDuration();
					}
					if (min_element > note.getDuration() && note.getDuration() > 0) {
						min_element = note.getDuration();
					}
				}

				int F = (int) Math.ceil(max_this / min_element);
				int C = (int) Math.ceil(max_element / min_this);

				// System.out.println("TAILLE DES ELEMENTS a = " +
				// this.sequence.size() + " b = " + ((SequenceElement)
				// anotherElement).sequence.size());

				int i = 0, j = 0;

				for (Note a : this.sequence) {
					for (Note b : element.sequence) {

						double deletion = distance[i][j + 1] + Similarity.weightDeletion(a);
						double insertion = distance[i + 1][j] + Similarity.weightInsertion(b);
						double fragmentation = Similarity.weightFragmentation(this.sequence, element.sequence, i + 1,
								j + 1, F, distance);
						double consolidation = Similarity.weightConsolidation(this.sequence, element.sequence, i + 1,
								j + 1, C, distance);
						double replacement = distance[i][j] + Similarity.weightReplacement(a, b);

						distance[i + 1][j + 1] = Similarity.min(deletion, insertion, fragmentation, consolidation,
								replacement);
						j++;
					}
					i++;
					j = 0;
				}

				// System.out.println("RESULT OF COMPARISON " +
				// (100-distance[m][n])/100);
				// System.out.println("distance = " + distance[m][n]);

				/*
				 * for(int k = 0; k < m+1; k++) { for(int f = 0; f < n+1; f++) {
				 * System.out.print(Math.ceil(distance[k][f]) + "	"); }
				 * System.out.println(/*distance[k][k]); }
				 */

				if ((100 - distance[m][n]) / 100 >= 0.7) {
					return 1.0;
				}

				return (100 - distance[m][n]) / 100;
			}
		}
		return 0;
	}

	@Override
	public String getText() {
		String text = "";
		for (Note note : sequence) {
			text += " " + note.getPitch() + note.getAccidental();
		}
		return text;
	}

	public int getSize() {
		return sequence.size();
	}

	@Override
	public ArrayList<String> getWords() {
		String key = PartUtils.getKeyOfPart(sequence);

		ArrayList<String> words = new ArrayList<String>();

		words.add(key);

		return words;
	}

}
