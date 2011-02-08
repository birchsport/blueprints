package com.tinkerpop.blueprints.pgm.impls.jena;

import java.util.HashSet;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.impls.StringFactory;

public class JenaVertex extends JenaElement implements Vertex {

	public JenaVertex(final Model model, final RDFNode rdfNode) {
		this.model = model;
		this.rdfNode = rdfNode;
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
		final JenaVertex other = (JenaVertex) obj;
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
		return rdfNode.asResource().getURI();
	}

	@Override
	public Iterable<Edge> getInEdges() {
		final Set<Edge> edges = new HashSet<Edge>();
		final StmtIterator statements = model
				.listStatements(new SimpleSelector() {

					@Override
					public boolean selects(final Statement s) {
						if (s.getObject().isResource()
								&& s.getSubject().isResource()
								&& rdfNode.asResource().getURI().equals(
										s.getObject().asResource().getURI())) {
							return true;
						}
						return false;
					}

				});
		while (statements.hasNext()) {
			final Statement statement = statements.next();
			edges.add(new JenaEdge(model, statement.getPredicate(), statement
					.getSubject(), statement.getObject()));
		}
		return edges;
	}

	@Override
	public Iterable<Edge> getOutEdges() {
		final Set<Edge> edges = new HashSet<Edge>();
		final StmtIterator statements = model
				.listStatements(new SimpleSelector() {

					@Override
					public boolean selects(final Statement s) {
						if (s.getSubject().isResource()
								&& s.getObject().isResource()
								&& rdfNode.asResource().getURI().equals(
										s.getSubject().asResource().getURI())) {
							return true;
						}
						return false;
					}

				});
		while (statements.hasNext()) {
			final Statement statement = statements.next();
			edges.add(new JenaEdge(model, statement.getPredicate(), statement
					.getSubject(), statement.getObject()));
		}
		return edges;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rdfNode == null) ? 0 : rdfNode.hashCode());
		return result;
	}

	@Override
	public Object removeProperty(final String key) {
		final StmtIterator statements = model
				.listStatements(new SimpleSelector() {

					@Override
					public boolean selects(final Statement s) {
						if (rdfNode.asResource().getURI().equals(
								s.getSubject().asResource().getURI())
								&& s.getPredicate().getURI().equals(key)) {
							return true;
						}
						return false;
					}

				});
		if (statements.hasNext()) {
			model.remove(statements.next());
		}
		return key;
	}

	@Override
	public String toString() {
		return StringFactory.vertexString(this);
	}
}
