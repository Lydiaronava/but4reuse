package org.but4reuse.adapters.music;

import org.but4reuse.adapters.IDependencyObject;

/**
 * 
 * @author Lydia Rodriguez-de la Nava
 *
 */

public class KeyDependencyObject implements IDependencyObject {

	public String key;

	public KeyDependencyObject(String key) {
		this.key = key;
	}

	public boolean equals(Object obj) {
		if (obj instanceof KeyDependencyObject) {
			return !key.equals(((KeyDependencyObject) obj).key);
		}
		return super.equals(obj);

	}

	@Override
	public int getMinDependencies(String dependencyID) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxDependencies(String dependencyID) {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public String getDependencyObjectText() {
		// TODO Auto-generated method stub
		return key;
	}

}
