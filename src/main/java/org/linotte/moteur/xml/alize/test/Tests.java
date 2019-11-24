package org.linotte.moteur.xml.alize.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Tests implements Iterator<Test> {

	private List<Test> tests = new ArrayList<Test>();
	private int position = 0;

	@Override
	public boolean hasNext() {
		return position < tests.size();
	}

	@Override
	public Test next() {
		Test test = tests.get(position);
		position++;
		return test;
	}

	@Override
	public void remove() {
	}

	public void add(Test test) {
		tests.add(test);
		test.numero = tests.size();
	}

}
