package it.polito.tdp.food.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.food.db.FoodDao;

public class Model {
	
	private FoodDao dao;
	private Graph<String, DefaultWeightedEdge> grafo;
	private Map<String, PortionWithWeight> predecessori;
	private int pesoMax;
	private List<String> cammino;
	
	public Model() {
		this.dao = new FoodDao();
	}
	
	public void creaGrafo(int c) {
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		//aggiungo i vertici
		Graphs.addAllVertices(grafo, this.dao.getPortionsByCal(c));
		
		//aggiungo gli archi
		for(Adiacenza a : this.dao.getEdges(c)) {
			if(this.grafo.vertexSet().contains(a.getPorzione1()) && this.grafo.vertexSet().contains(a.getPorzione2()))
			Graphs.addEdgeWithVertices(grafo, a.getPorzione1(), a.getPorzione2(), a.getPeso());
		}
		
	}
	
	public int getNumVertex() {
		return this.grafo.vertexSet().size();
	}
	public int getNumEdges() {
		return this.grafo.edgeSet().size();
	}
	
	public List<String> getPortions(){
		List<String> portions = new LinkedList<>(this.grafo.vertexSet());
		Collections.sort(portions);
		return portions;
	}
	
	public String getCorrelate(String portion){
		
		List<PortionWithWeight> correlate = new LinkedList<PortionWithWeight>();
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			if(grafo.getEdgeSource(e).equals(portion))
				correlate.add(new PortionWithWeight(grafo.getEdgeTarget(e), (int)grafo.getEdgeWeight(e)));
			else if(grafo.getEdgeTarget(e).equals(portion))
				correlate.add(new PortionWithWeight(grafo.getEdgeSource(e), (int)grafo.getEdgeWeight(e)));
		}
		String result = "";
		if(correlate.isEmpty())
			result += "Non ci sono porzioni correlate\n";
		else {
			for(PortionWithWeight p : correlate) {
				result += p.getPortion()+" "+p.getPeso()+"\n";
			}

		}
		return result;
		
	}
	
	public String getCammino(String partenza, int N) {
		//individuo i veritici raggiungibili dalla partenza
		this.setRaggiungibili(partenza);
		
		if(N>this.predecessori.size()) {
			return "Il numero di passi eccede il numero di vertici raggiungibili";
		}
		//utilizzando un metodo ricorsivo individuo la combinazione di
		//vertici consecutiva che mi da il cammino a peso massimo
		//e rispetta gli altri vincoli del problema
		this.pesoMax = 0;
		List<String> parziale = new LinkedList<>();
		parziale.add(partenza);
		this.cammino = null;
		this.cerca(parziale, 0, N);
		
		
		if(this.cammino == null) {
			return "Non esiste un cammino\n";
		}
		String result = "";
		for(String p : this.cammino) {
			result += p+"\n";
		}
		return result+"Peso totale: "+this.pesoMax+"\n";
	}
	
	public void cerca(List<String> parziale, int peso, int N) {
		//condizione terminale
		if(parziale.size()==(N+1)) {
			if(peso>pesoMax) {
				pesoMax = peso;
				this.cammino = new LinkedList<>(parziale);
			}
			return;
		}
		
		for(String p : this.predecessori.keySet()) {
			//se questa porzione ha un predecessore nel cammino
			if(predecessori.get(p)!=null) {
				//se non ho già memorizzato questa porzione nella soluzione e se l'ultima
				//porzione inserita è il suo predecessore
				if(parziale.get(parziale.size()-1).equals(predecessori.get(p).getPortion()) && !parziale.contains(p)) {
					parziale.add(p);
					this.cerca(parziale, peso+this.predecessori.get(p).getPeso(), N);
					
					//backtracking
					parziale.remove(p);
				}
			}
			
		}
	}
	
	public void setRaggiungibili(String partenza) {
		BreadthFirstIterator<String, DefaultWeightedEdge> bfi = new BreadthFirstIterator<String, DefaultWeightedEdge>(grafo, partenza);
		this.predecessori = new HashMap<>();
		this.predecessori.put(partenza, null);
		bfi.addTraversalListener(new TraversalListener<String, DefaultWeightedEdge>() {

			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultWeightedEdge> e) {
				
				DefaultWeightedEdge arco = e.getEdge();
				String p1 = grafo.getEdgeSource(arco);
				String p2 = grafo.getEdgeTarget(arco);
				int peso = (int)grafo.getEdgeWeight(arco);
				
				if(predecessori.containsKey(p1) && !predecessori.containsKey(p2)) {
					predecessori.put(p2, new PortionWithWeight(p1, peso));
				}else { if(predecessori.containsKey(p2) && !predecessori.containsKey(p1)) {
					predecessori.put(p1, new PortionWithWeight(p2, peso));
				}}
				
				
			}

			@Override
			public void vertexTraversed(VertexTraversalEvent<String> e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void vertexFinished(VertexTraversalEvent<String> e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		while(bfi.hasNext())
			bfi.next();
	}
	
	
	
	
}
