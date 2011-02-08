package com.tinkerpop.blueprints.pgm.impls.jena;

import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.Vertex;

public class JenaGraph implements Graph {

	private Model model;

	public JenaGraph() {
		this((Model) null);
	}

	public JenaGraph(final Model model) {
		if (model == null) {
			this.model = ModelFactory.createDefaultModel();
		} else {
			this.model = model;
		}
	}

	public JenaGraph(final String rdfXml) {
		final Model model = ModelFactory.createMemModelMaker()
				.createDefaultModel();
		model.read(new StringReader(rdfXml), null);
		this.model = model;
	}

	@Override
	public Edge addEdge(final Object id, final Vertex outVertex,
			final Vertex inVertex, final String label) {
		final Resource inV = model.createResource(inVertex.getId().toString());
		final Resource outV = model
				.createResource(outVertex.getId().toString());
		final Property edge = model.createProperty(label);
		final Statement statement = model.createStatement(outV, edge, inV);
		model.add(statement);
		return new JenaEdge(model, edge, inV, outV);
	}

	@Override
	public Vertex addVertex(final Object id) {
		if (id == null) {
			Resource createResource = model.createResource();
			return new JenaVertex(model, createResource);
		}
		return new JenaVertex(model, model.createResource(id.toString()));
	}

	@Override
	public void clear() {
		model.removeAll();
	}

	@Override
	public Edge getEdge(final Object id) {
		final String[] keys = id.toString().split("->");
		final StmtIterator statements = model
				.listStatements(new SimpleSelector() {

					@Override
					public boolean selects(final Statement s) {
						if (s.getObject().isResource()
								&& s.getSubject().isResource()
								&& s.getSubject().asResource().getURI().equals(
										keys[0])
								&& s.getPredicate().asResource().getURI()
										.equals(keys[1])
								&& s.getObject().asResource().getURI().equals(
										keys[2])) {
							return true;
						}
						return false;
					}

				});
		JenaEdge edge = null;
		if (statements.hasNext()) {
			final Statement statement = statements.next();
			final Property predicate = statement.getPredicate();
			edge = new JenaEdge(model, predicate, statement.getSubject(),
					statement.getObject());
		}
		return edge;
	}

	@Override
	public Iterable<Edge> getEdges() {
		final Set<Edge> edges = new HashSet<Edge>();
		final StmtIterator statements = model.listStatements();
		while (statements.hasNext()) {
			final Statement statement = statements.next();
			final Property predicate = statement.getPredicate();
			if (statement.getSubject().isResource()
					&& statement.getObject().isResource()) {
				edges.add(new JenaEdge(model, predicate,
						statement.getSubject(), statement.getObject()));
			}
		}
		return edges;
	}

	@Override
	public Vertex getVertex(final Object id) {
		final Resource resource = model.getResource(id.toString());
		return new JenaVertex(model, resource);
	}

	@Override
	public Iterable<Vertex> getVertices() {
		final Set<Vertex> vertices = new HashSet<Vertex>();
		final StmtIterator statements = model.listStatements();
		while (statements.hasNext()) {
			final Statement statement = statements.next();
			final Resource resource = statement.getSubject();
			if (resource.isResource()) {
				vertices.add(new JenaVertex(model, resource));
			}
			final RDFNode object = statement.getObject();
			if (object.isResource()) {
				vertices.add(new JenaVertex(model, object));
			}
		}
		return vertices;
	}

	@Override
	public void removeEdge(final Edge edge) {
		final String[] keys = edge.getId().toString().split("->");
		final StmtIterator statements = model
				.listStatements(new SimpleSelector() {

					@Override
					public boolean selects(final Statement s) {
						if (s.getSubject().asResource().getURI()
								.equals(keys[0])
								&& s.getPredicate().asResource().getURI()
										.equals(keys[1])
								&& s.getObject().asResource().getURI().equals(
										keys[2])) {
							return true;
						}
						return false;
					}

				});
		if (statements.hasNext()) {
			final Statement statement = statements.next();
			model.remove(statement);
		}
	}

	@Override
	public void removeVertex(final Vertex vertex) {
		// find all statements that have the Vertex id (URI) as either
		// the subject or the object
		if(vertex.getId() == null) {
			return;
		}
		final StmtIterator statements = model
				.listStatements(new SimpleSelector() {
					@Override
					public boolean selects(final Statement s) {
						final Resource subject = s.getSubject();
						final RDFNode object = s.getObject();
						if (subject.getURI().equals(vertex.getId().toString())) {
							return true;
						} else if (object.isResource()
								&& object.asResource().getURI().equals(
										vertex.getId().toString())) {
							return true;
						}
						return false;
					}
				});
		// anything that matched should be removed
		final List<Statement> statementsToRemove = new ArrayList<Statement>();
		while (statements.hasNext()) {
			final Statement statement = statements.next();
			statementsToRemove.add(statement);
		}
		for (final Statement statement : statementsToRemove) {
			model.remove(statement);
		}
	}

	public void write(OutputStream out) {
		model.write(out);
	}

	public String getRDFXML() {
		StringWriter writer = new StringWriter();
		model.write(writer);
		return writer.getBuffer().toString();
	}

	@Override
	public void shutdown() {

	}

}
