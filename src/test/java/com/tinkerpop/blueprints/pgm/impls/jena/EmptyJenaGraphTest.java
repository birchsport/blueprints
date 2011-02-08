package com.tinkerpop.blueprints.pgm.impls.jena;

import java.lang.reflect.Method;
import java.util.Set;

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
public class EmptyJenaGraphTest extends GraphTest {

	private static final String NAMESPACE = "http://mvm.com/owl/2010/10/mm.owl#";

//	public void testVertexTestSuite() throws Exception {
//		doTestSuite(new VertexTestSuite(this));
//	}
//
//	public void testEdgeTestSuite() throws Exception {
//		doTestSuite(new EdgeTestSuite(this));
//	}
//
//	public void testGraphTestSuite() throws Exception {
//		doTestSuite(new GraphTestSuite(this));
//	}

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
		for (Method method : testSuite.getClass().getDeclaredMethods()) {
			if (method.getName().startsWith("test")) {
				System.out.println("Testing " + method.getName() + "...");
				method.invoke(testSuite);
			}
		}
	}

	@Override
	public Graph getGraphInstance() {

		Graph graphInstance = new JenaGraph();
		Vertex sV = graphInstance.addVertex(NAMESPACE + "S");
		sV.setProperty(NAMESPACE + "name", "someName");
		Vertex pV = graphInstance.addVertex(NAMESPACE + "P");
		pV.setProperty(NAMESPACE + "name", "someName");
		Vertex tV = graphInstance.addVertex(NAMESPACE + "T");
		tV.setProperty(NAMESPACE + "name", "someName");
		Vertex uV = graphInstance.addVertex(NAMESPACE + "U");
		uV.setProperty(NAMESPACE + "name", "someName");
		graphInstance.addEdge(null, sV, pV, NAMESPACE + "connectedTo");
		graphInstance.addEdge(null, sV, uV, NAMESPACE + "connectedTo");
		graphInstance.addEdge(null, pV, tV, NAMESPACE + "connectedTo");
		graphInstance.addEdge(null, tV, uV, NAMESPACE + "connectedTo");
		return graphInstance;
	}

	private int getSize(Iterable iterable) {
		int size = 0;
		for (Object object : iterable) {
			size++;
		}
		return size;
	}

}
