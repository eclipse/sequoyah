package org.eclipse.sequoyah.localization.tools.datamodel.node;

public class StringArrayItemNode extends StringNode {

	private StringArrayNode parent;

	private int position = 0;

	public StringArrayItemNode(String value, StringArrayNode parent,
			int position) {
		super(parent.getKey(), value);
		this.parent = parent;
		this.position = position;
	}

	public StringArrayNode getParent() {
		return parent;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	@Override
	public String toString() {
		return position + ": " + value; //$NON-NLS-1$
	}
}
