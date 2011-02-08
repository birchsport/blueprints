package com.tinkerpop.blueprints.pgm.impls.jena;

import java.util.Set;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;
import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.EdgeTestSuite;
import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.GraphTestSuite;
import com.tinkerpop.blueprints.pgm.TestSuite;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.VertexTestSuite;
import com.tinkerpop.blueprints.pgm.impls.GraphTest;

/**
 * @author James Birchfield
 */
public class JenaGraphTest extends GraphTest {

	private static final String NAMESPACE = "http://mvm.com/owl/2010/10/mm.owl#";

	public void testVertexTestSuite() throws Exception {
		doTestSuite(new VertexTestSuite(this));
	}

	public void testEdgeTestSuite() throws Exception {
		doTestSuite(new EdgeTestSuite(this));
	}

	public void testGraphTestSuite() throws Exception {
		doTestSuite(new GraphTestSuite(this));
	}

	public void testGetRDFXML() throws Exception {
		JenaGraph graph = (JenaGraph) this.getGraphInstance();
		assertNotNull(graph.getRDFXML());
	}
	
	public void testGetVerticies() throws Exception {
		Iterable<Vertex> vertices = this.getGraphInstance().getVertices();
		assertNotNull(vertices);
		assertEquals(4, getSize(vertices));
	}

	public void testGetRemoveEdges() throws Exception {
		Graph graphInstance = this.getGraphInstance();
		Iterable<Edge> edges = graphInstance.getEdges();
		assertNotNull(edges);
		for (Edge edge : edges) {
			graphInstance.removeEdge(edge);
		}
		edges = graphInstance.getEdges();
		assertNotNull(edges);
		assertEquals(0, getSize(edges));
	}

	public void testGetRemoveVertices() throws Exception {
		Graph graphInstance = this.getGraphInstance();
		Iterable<Vertex> vertices = graphInstance.getVertices();
		assertNotNull(vertices);
		for (Vertex vertex : vertices) {
			graphInstance.removeVertex(vertex);
		}
		vertices = graphInstance.getVertices();
		assertNotNull(vertices);
		assertEquals(0, getSize(vertices));

	}

	public void testGetEdge() throws Exception {
		String id = NAMESPACE + "S->" + NAMESPACE + "connectedTo->" + NAMESPACE
				+ "P";
		Edge edge = this.getGraphInstance().getEdge(id);
		assertNotNull(edge);
		assertEquals(NAMESPACE + "connectedTo", edge.getLabel());
		assertEquals(id, edge.getId());
	}

	public void testGetEdgeProperty() throws Exception {
		String id = NAMESPACE + "S->" + NAMESPACE + "connectedTo->" + NAMESPACE
				+ "P";
		Edge edge = this.getGraphInstance().getEdge(id);
		assertNotNull(edge);
		Object property = edge.getProperty(NAMESPACE + "name");
		assertNull(property);

	}

	public void testGetVertex() throws Exception {
		Vertex vertex = this.getGraphInstance().getVertex(NAMESPACE + "S");
		assertNotNull(vertex);
	}

	public void testGetVertexInEdges() throws Exception {
		Vertex vertex = this.getGraphInstance().getVertex(NAMESPACE + "U");
		assertNotNull(vertex);
		Iterable<Edge> inEdges = vertex.getInEdges();
		assertNotNull(inEdges);
		assertEquals(2, getSize(inEdges));
	}

	public void testGetVertexOutEdges() throws Exception {
		Vertex vertex = this.getGraphInstance().getVertex(NAMESPACE + "S");
		assertNotNull(vertex);
		Iterable<Edge> outEdges = vertex.getOutEdges();
		assertNotNull(outEdges);
		assertEquals(2, getSize(outEdges));
	}

	public void testGetVertexPropertyKeys() throws Exception {
		Vertex vertex = this.getGraphInstance().getVertex(NAMESPACE + "S");
		assertNotNull(vertex);
		Set<String> propertyKeys = vertex.getPropertyKeys();
		assertNotNull(propertyKeys);
		assertEquals(1, propertyKeys.size());
	}

	public void testGetVertexProperty() throws Exception {
		Vertex vertex = this.getGraphInstance().getVertex(NAMESPACE + "S");
		assertNotNull(vertex);
		Object property = vertex.getProperty(NAMESPACE + "name");
		assertNotNull(property);
		assertEquals("someName", property.toString());
	}

	public void testGetEdges() throws Exception {
		Iterable<Edge> edges = this.getGraphInstance().getEdges();
		assertNotNull(edges);
		assertEquals(4, getSize(edges));
	}

	@Override
	public void doTestSuite(TestSuite testSuite) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public Graph getGraphInstance() {
		OntModel model = ModelFactory
				.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		model.setDerivationLogging(false);
		model.setDynamicImports(false);
		model.setStrictMode(true);
		model.setNsPrefix("mm", NAMESPACE);
		createLink(model, "S", "P");
		createLink(model, "S", "U");
		createLink(model, "P", "T");
		createLink(model, "T", "U");
		return new JenaGraph(model);
	}

	private void createLink(OntModel model, String subject, String object) {
		Statement statement = model.createStatement(model
				.createResource(NAMESPACE + subject), model
				.createProperty(NAMESPACE + "connectedTo"), model
				.createResource(NAMESPACE + object));
		model.add(statement);

		Statement statement1 = model.createStatement(model
				.createResource(NAMESPACE + subject), model
				.createProperty(NAMESPACE + "name"), model
				.createLiteral("someName"));
		model.add(statement1);

	}

	private int getSize(Iterable iterable) {
		int size = 0;
		for (Object object : iterable) {
			size++;
		}
		return size;
	}

}
