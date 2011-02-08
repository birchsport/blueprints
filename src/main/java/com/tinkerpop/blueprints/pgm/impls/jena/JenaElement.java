package com.tinkerpop.blueprints.pgm.impls.jena;

import java.util.HashSet;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.tinkerpop.blueprints.pgm.Element;

public abstract class JenaElement implements Element {

	protected RDFNode rdfNode;
	protected Model model;

	@Override
	public Object getProperty(final String key) {
		if (!rdfNode.isResource()) {
			return null;
		}
		final Statement resource = rdfNode.asResource().getProperty(
				model.createProperty(key));
		if (resource == null) {
			return null;
		}
		return resource.getObject().asLiteral().getString();
	}

	@Override
	public Set<String> getPropertyKeys() {
		final Set<String> properties = new HashSet<String>();
		final StmtIterator iterator = rdfNode.asResource().listProperties();
		while (iterator.hasNext()) {
			final Statement statement = iterator.next();
			if (statement.getObject().isLiteral()) {
				properties.add(statement.getPredicate().getURI());
			}
		}
		return properties;
	}

	@Override
	public Object removeProperty(final String key) {
		final StmtIterator statements = model
				.listStatements(new SimpleSelector() {

					@Override
					public boolean selects(final Statement s) {
						if (s.getObject().isLiteral()
								&& s.getSubject().isResource()
								&& s.getSubject().asResource().getURI().equals(
										key)
								&& s.getSubject().asResource().getURI().equals(
										rdfNode.asResource().getURI())) {
							return true;
						}
						return false;
					}

				});
		Object property = null;
		if (statements.hasNext()) {
			final Statement statement = statements.next();
			property = statement.getObject().asLiteral().getString();
		}
		return property;
	}

	@Override
	public void setProperty(final String key, final Object value) {
		final Statement statement = model.createStatement(model
				.createResource(rdfNode.asResource().getURI()), model
				.createProperty(key), model.createLiteral(value.toString()));
		model.add(statement);

	}

}
