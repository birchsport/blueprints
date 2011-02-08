package com.tinkerpop.blueprints.pgm.impls.jena;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.impls.StringFactory;

public class JenaEdge extends JenaElement implements Edge {

	private final RDFNode inVertex;
	private final RDFNode outVertex;

	public JenaEdge(final Model model, final RDFNode rdfNode,
			final RDFNode inVertex, final RDFNode outVertex) {
		this.model = model;
		this.rdfNode = rdfNode;
		this.inVertex = inVertex;
		this.outVertex = outVertex;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final JenaEdge other = (JenaEdge) obj;
		if (inVertex == null) {
			if (other.inVertex != null) {
				return false;
			}
		} else if (!inVertex.equals(other.inVertex)) {
			return false;
		}
		if (outVertex == null) {
			if (other.outVertex != null) {
				return false;
			}
		} else if (!outVertex.equals(other.outVertex)) {
			return false;
		}
		if (rdfNode == null) {
			if (other.rdfNode != null) {
				return false;
			}
		} else if (!rdfNode.equals(other.rdfNode)) {
			return false;
		}
		return true;
	}

	@Override
	public Object getId() {
		return inVertex.asResource().getURI() + "->"
				+ rdfNode.asResource().getURI() + "->"
				+ outVertex.asResource().getURI();
	}

	@Override
	public Vertex getInVertex() {
		return new JenaVertex(model, inVertex);
	}

	@Override
	public String getLabel() {
		return rdfNode.asResource().getURI();
	}

	@Override
	public Vertex getOutVertex() {
		return new JenaVertex(model, outVertex);
	}

	@Override
	public Object getProperty(final String key) {
		return model.getProperty(rdfNode.asResource(), model
				.createProperty(key));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((inVertex == null) ? 0 : inVertex.hashCode());
		result = prime * result
				+ ((outVertex == null) ? 0 : outVertex.hashCode());
		result = prime * result + ((rdfNode == null) ? 0 : rdfNode.hashCode());
		return result;
	}

	@Override
	public Object removeProperty(final String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProperty(final String key, final Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		return StringFactory.edgeString(this);
	}

}
