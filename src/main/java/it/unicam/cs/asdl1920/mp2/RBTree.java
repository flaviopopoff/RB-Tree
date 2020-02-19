package it.unicam.cs.asdl1920.mp2;

import java.util.ArrayList;
import java.util.List;

/**
 * Un RBTree, un albero rosso-nero, è un albero binario di ricerca con le
 * seguenti proprietà:
 * 
 * 1- Ciascun nodo è rosso o nero e la radice è sempre nera
 * 
 * 2- Ciascuna foglia NIL (cioè una foglia che non contiene una etichetta di
 * tipo E) è nera
 * 
 * 3- Se un nodo è rosso allora entrambi i figli sono neri
 * 
 * 4- Ogni cammino da un nodo ad una foglia sua discendente contiene lo stesso
 * numero di nodi neri (contando anche le foglie NIL)
 * 
 * Si può dimostrare che le operazioni di inserimento, ricerca e rimozione di un
 * valore in un RBTree hanno un costo O(lg n), dove n è il numeo di nodi
 * dell'albero. Ciò è dovuto al fatto che la cosiddetta altezza nera (cioè il
 * numero dei nodi neri incontrati in un cammino) viene mantenuta uguale per
 * tutti i cammini dalla radice alle foglie.
 * 
 * Per maggiori dettagli si veda il Cap. 13 di T.H. Cormen, C.E. Leiserson, R.L.
 * Rivest, C. Stein, Introduzione agli Algoritmi e Strutture Dati (terza
 * edizione), McGraw-Hill, 2010 -
 * https://www.mheducation.it/9788838665158-italy-introduzione-agli-algoritmi-e-strutture-dati-3ed
 * 
 * In questa implementazione degli RBTree è possibile inserire elementi
 * duplicati, ma non è possibile inserire elementi null.
 * 
 * @author Flavio Pocari
 *         flavio.pocari@studenti.unicam.it
 *
 * @param <E>
 *                il tipo delle etichette dei nodi dell'albero. La classe E deve
 *                avere un ordinamento naturale implementato tramite il metodo
 *                compareTo. Tale ordinamento è quello usato nell'RBTree per
 *                confrontare le etichette dei nodi.
 * 
 * 
 */
public class RBTree<E extends Comparable<E>> {
	
    /*
     * Costanti e metodi static.
     */
	
    /*
     * E' un flag che rappresenta il colore rosso dei nodi negli RBTree.
     */
    protected static final boolean RED = true;
    
    /*
     * E' un flag che rappresenta il colore rosso dei nodi negli RBTree.
     */
    protected static final boolean BLACK = false;

    
    /**
     * Determina se un nodo di un albero RBTree è rosso.
     * 
     * @param x
     *              un nodo di un albero RBTree
     * 
     * @return true se il nodo passato è colorato di rosso, false altrimenti
     */
    protected static boolean isRed(
            @SuppressWarnings("rawtypes") RBTree.RBTreeNode x) {
        if (isNil(x)) {
            return false;
        }
        
        return x.color == RED;
    }
     
    
    /**
     * Determina se un nodo di un albero RBTree è nero.
     * 
     * @param x
     *              un nodo di un albero RBTree
     * 
     * @return true se il nodo passato è colorato di nero oppure se è una foglia
     *         NIL (che è sempre considerata un nodo nero in un RBTree), false
     *         altrimenti
     */
    protected static boolean isBlack(
            @SuppressWarnings("rawtypes") RBTree.RBTreeNode x) {
        if (isNil(x)) {
            return true;
        }
        
        return x.color == BLACK;
    }
    
    
    /**
     * Determina se un certo nodo è una foglia NIL.
     * 
     * E' possibile rappresentare le foglie NIL sia con il valore null sia con
     * un particolare nodo "sentinella". Questa API è indipendente dalla
     * particolare scelta implementativa poiché fa riferimento al concetto
     * astratto di foglia NIL negli alberi rosso-neri.
     * 
     * @param n
     *              un puntatore a un nodo di un RBTRee
     * 
     * @return true se il puntatore passato punta a una foglia NIL.
     */
    protected static boolean isNil(
            @SuppressWarnings("rawtypes") RBTree.RBTreeNode n) {
    	return (n == null) ? false : n.el == null;
    }
    
    
    /*
     * Variabili istanza e metodi non static.
     */

    /*
     * Il nodo radice di questo albero. Se null, allora l'albero è vuoto.
     */
    private RBTreeNode root;
    
    /*
     * Oggetto sentinella che raccoglie tutti i figli destri e sinistri delle foglie.
     */
    private RBTreeNode sentinella = new RBTreeNode();
    
    /*
     * Il numero di elementi in questo RBTree, diverso dal numero di nodi poiché
     * un elemento può essere inserito più di una volta e ciò non incrementa il
     * numero di nodi, ma solo un contatore di molteplicità nel nodo che
     * contiene l'elemento ripetuto.
     */
    private int size;
    
    /*
     * Il numero di nodi in questo RBTree.
     */
    private int numberOfNodes;    
    
    
    /**
     * Costruisce un RBTree che consiste solo di un nodo radice.
     * 
     * @param rootElement
     *                        l'informazione associata al nodo radice
     * @throws NullPointerException
     *                                  se l'elemento passato è null
     */
    public RBTree(E element) {
    	
        if(element == null) {
        	throw new NullPointerException();
        }
        
        this.root = new RBTreeNode(element);
        this.size = this.numberOfNodes = 1;
        this.root.color = BLACK;
    }
    
    
    /*
     * Costruisce un RBTree sentinella.
     */
    public RBTree() {
        this.root = sentinella;
    }
   
    
    /**
     * Determina se questo RBTree contiene un certo elemento.
     * 
     * @param el
     *               l'elemento da cercare
     * @return true se l'elemento è presente in questo RBTree, false altrimenti.
     * @throws NullPointerException
     *                                  se l'elemento {E el} è null
     */
	public boolean contains(E el) {
		
		if (el == null) {
			throw new NullPointerException();
		}
		
		/*
		 * Se il valore non è presente nell'albero restituisco il valore falso, true altrimenti.
		 */
		return this.root.search(el) != null ? true : false;
	}
    
	
    /**
     * Restituisce il nodo che è etichettato con un certo elemento dato.
     * 
     * @param el
     *               l'elemento che etichetta il nodo cercato
     * 
     * @return il puntatore al nodo che è etichettato con l'elemento dato,
     *         oppure null se nell'albero non c'è nessun nodo etichettato con
     *         quell'elemento
     * 
     * @throws NullPointerException
     *                                  se l'elemento passato è null
     * 
     */
	protected RBTreeNode getNodeOf(E el) {
		
		if (el == null) {
			throw new NullPointerException();
		}
		
		/*
		 * Se l'elemento è presente nell' albero restituisco il nodo a cui esso è etichettato,
		 * altrimenti restituisco il valore null.
		 */
		return this.contains(el) ? this.root.search(el) : null;
	}
    
	
    /**
     * Determina il numero di occorrenze di un certo elemento in questo RBTree.
     * 
     * @param el
     *               l'elemento di cui determinare il numero di occorrenze
     * @return il numero di occorrenze dell'elemento {E el} in questo RBTree,
     *         zero se non è presente.
     * @throws NullPointerException
     *                                  se l'elemento {E el} è null
     */
    public int getCount(E el) {
    	
    	if (el == null) {
    		throw new NullPointerException();
    	}
    	
		/*
		 * Se il valore è presente nell'albero conto le occorrenze che esso presenta
		 * altrimenti restituisco il valore zero, cioe l'elemento non è presente
		 * all'interno di esso.
		 */
    	return this.contains(el) ? this.root.search(el).getCount() : 0;
    }

    
    /**
     * Restituisce l'altezza nera di questo RBTree, cioè il numero di nodi
     * colorati di nero in un qualsiasi cammino dalla radice a una foglia NIL.
     * Se questo RBTree è vuoto viene restituito il valore -1. L'altezza nera è
     * sempre maggiore o uguale di 1 in un albero non vuoto.
     * 
     * @return l'altezza di questo RBTree, -1 se questo RBTree è vuoto.
     */
    public int getBlackHeight() {

		/*
		 * Se questo albero è vuoto allora restituisco il valore -1 altrimenti restituisco
		 * il valore dell'altezza dei nodi neri che si trovano lungo il cammino.
		 */
        return this.isEmpty() ? -1 : this.root.getBlackHeight();
    }
    
    
    /**
     * Restituisce l'elemento minimo presente in questo RBTree.
     * 
     * @return l'elemento minimo in questo RBTree
     * @return null se questo RBTree è vuoto.
     */
    public E getMinimum() {
        return this.isEmpty() ? null : this.root.getMinimum().getEl();
    }
    
    
    /**
     * Restituisce l'elemento massimo presente in questo RBTree.
     * 
     * @return l'elemento massimo in questo RBTree
     * @return null se questo RBTree è vuoto.
     */
    public E getMaximum() {
        return this.isEmpty() ? null : this.root.getMaximum().getEl();
    }

    
    /**
     * Restituisce il numero di nodi in questo RBTree.
     * 
     * @return il numero di nodi in questo RBTree.
     */
    public int getNumberOfNodes() {
        return this.numberOfNodes;
    }

    
    /**
     * Restituisce l'elemento <b>strettamente</b> predecessore, in questo
     * RBTree, di un dato elemento. Si richiede che l'elemento passato sia
     * presente nell'albero.
     * 
     * @param el
     *               l'elemento di cui si chiede il predecessore
     * @return l'elemento <b>strettamente</b> predecessore rispetto
     *         all'ordinamento naturale della classe {@code E}, di {@code el} in
     *         questo RBTree, oppure {@code null} se {@code el} è l'elemento
     *         minimo.
     * @throws NullPointerException
     *                                      se l'elemento {@code el} è null
     * @throws IllegalArgumentException
     *                                      se l'elemento {@code el} non è
     *                                      presente in questo RBTree.
     */
    public E getPredecessor(E el) {
    	
    	if(el == null) {
    		throw new NullPointerException();
    	}

    	if(!this.contains(el)) {
    		throw new IllegalArgumentException();
    	}
    	
		/*
		 * Se l'elemento coincide con il minimo allora restituisco il valore nullo, in quanto non si
		 * ha un predecessore, altrimenti restituisco il predecessore.
		 */		
        return (el.equals(this.getMinimum())) ? null : this.root.search(el).getPredecessor().getEl();
    }
    
    
    /**
     * Restituisce l'elemento <b>strettamente</b> successore, in questo RBTree,
     * di un dato elemento. Si richiede che l'elemento passato sia presente
     * nell'albero.
     * 
     * @param el
     *               l'elemento di cui si chiede il successore
     * @return l'elemento <b>strettamente</b> successore, rispetto
     *         all'ordinamento naturale della classe {@code E}, di {@code el} in
     *         questo RBTree, oppure {@code null} se {@code el} è l'elemento
     *         massimo.
     * @throws NullPointerException
     *                                      se l'elemento {@code el} è null
     * @throws IllegalArgumentException
     *                                      se l'elemento {@code el} non è
     *                                      presente in questo RBTree.
     */
    public E getSuccessor(E el) {
    	
    	if(el == null) {
    		throw new NullPointerException();
    	}

    	if(!this.contains(el)) {
    		throw new IllegalArgumentException();
    	}
    	
		/*
		 * Se l'elemento coincide con il massimo allora restituisco il valore nullo, in quanto non si
		 * ha un successore, altrimenti restituisco il successore.
		 */		
        return (el.equals(this.getMaximum())) ? null : this.root.search(el).getSuccessor().getEl();
    }

    
    /**
     * @return the root
     */
    protected RBTreeNode getRoot() {
        return this.root;
    }
    
    
    /**
     * Restituisce il numero di elementi contenuti in questo RBTree. In caso di
     * elementi ripetuti essi vengono contati più volte.
     * 
     * @return il numero di elementi di tipo {@code E} presenti in questo
     *         RBTree, zero se non è presente nessun elemento.
     */
    public int getSize() {
        return this.size;
    }
    
    
    /**
     * Restituisce la lista degli elementi contenuti in questo RBTree secondo
     * l'ordine determinato dalla visita in-order. Per le proprietà degli alberi
     * binari di ricerca la lista ottenuta conterrà gli elementi in ordine
     * crescente rispetto all'ordinamento naturale della classe {@code E}. Nel
     * caso di elementi ripetuti, essi appaiono più volte nella lista
     * consecutivamente.
     * 
     * @return la lista ordinata degli elementi contenuti in questo RBTree,
     *         tenendo conto della loro molteplicità.
     */
    public List<E> inOrderVisit() {
    	
    	List<E> rbTreeList = new ArrayList<E>();
    	this.root.inOrderVisit(rbTreeList);
    	
    	/*
    	 * Ritorna la lista modificata dal metodo ricorsivo con tutti gli elementi contenuti al suo
    	 * interno.
    	 */
        return rbTreeList;
    }

    
    /**
     * Determina se questo RBTree è vuoto.
     * 
     * @return true se questo RBTree è vuoto, false altrimenti.
     */
    public boolean isEmpty() {
        return isNil(this.root);
    }

    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String descr = "RBTree [root=" + root.el.toString() + ", size=" + size 
        		+ ", numberOfNodes=" + numberOfNodes + "]\n";
        return descr + this.root.toString();
    }

    
    /**
     * Inserisce un nuovo elemento in questo RBTree. Se l'elemento è già
     * presente viene incrementato il suo numero di occorrenze.
     * 
     * @param el
     *               l'elemento da inserire.
     * @return il numero di confronti tra elementi della classe {@code E}
     *         effettuati durante l'inserimento (cioè il numero di chiamate al
     *         metodo compareTo della classe E)
     * @throws NullPointerException
     *                                  se l'elemento {@code el} è null
     */
    public int insert(E el) {
    	
    	if(el == null) {
    		throw new NullPointerException();
    	}
    	
        return this.root.insert(el);
    }
    
    
    /**
     * Cancella da questo RBTree una occorrenza di un certo elemento.
     * 
     * @param element
     *                    l'elemento da cancellare
     * @return true, se l'elemento è stato cancellato, false se non era presente
     *         o l'albero è vuoto.
     * @throws NullPointerException
     *                                  se l'elemento passato è nullo
     */
    public boolean remove(E el) {
    	
    	if(el == null) {
    		throw new NullPointerException();
    	}
    	
        return this.root.remove(el);
    }

    
    /**
     * Classe interna per i nodi di un RBTree. La classe definisce i nodi
     * ricorsivamente, cioè un nodo contiene puntatori a nodi della stessa
     * classe. Le operazioni esposte come public nelle API della classe
     * principale RBTree<E> sono "duplicate" nei nodi e tipicamente il meodo
     * pubblico della classe principale fa dei controlli e poi, se è il caso,
     * chiama il metodo "gemello" sul nodo radice dell'albero.
     */
    protected class RBTreeNode {

        /*
         * Etichetta del nodo. ATTENZIONE: non può cambiare! Nel caso di
         * removeFixUp, bisogna utilizzare un metodo opportuno per sostituire il
         * nodo da rimuovere con il suo successore. Infatti non è possibole
         * cambiare l'etichetta associata al nodo da rimuovere. Attenzione al
         * caso di sostituzione della radice e al caso di sostituzione di un
         * nodo sul suo genitore!
         */
        protected final E el;
        
        // sottoalbero sinistro
        protected RBTreeNode left;
        
        // sottoalbero destro
        protected RBTreeNode right;
        
        // genitore del nodo, null se questo nodo è la radice del BRTree
        protected RBTreeNode parent;
        
        // colore del nodo
        protected boolean color;
        
        // conta il numero di occorrenze dell'elemento (molteplicità)
        protected int count;
        
        
        /**
         * Crea un nodo di un RBTree con l'etichetta passata. Il nodo creato non
         * ha nessun collegamento con altri nodi, ha molteplicità pari a 1 e
         * colore rosso.
         * 
         * @param el
         *               l'etichetta da associare al nodo
         * 
         * @throws NullPointerException
         *                                  se l'etichetta passata è null
         */
        protected RBTreeNode(E el) {
            this.el = el;
            this.left = this.right = this.parent = sentinella;
            this.color = RED;
            this.count = 1;
        }
        
        
        /*
         * Costruttore per la sentinella che ha elemento 'null'.
         */
        protected RBTreeNode() {
        	this.el = null;
        }

        
        /**
         * Metodo ricorsivo che implementa la visita in-order a partire dal nodo
         * this.
         * 
         * @param r
         *              la lista a cui aggiungere gli elementi visitati in-order
         */
        protected void inOrderVisit(List<E> r) {
        	
        	RBTreeNode nodo = this;
        	
            if(isNil(nodo)) {
            	return;
            }
            
            /*
             * Effettuo delle chiamate ricorsive simmetriche, cosi da aggiungere in maniera ordinata
             * gli elementi.
             */
            nodo.getLeft().inOrderVisit(r);
            
            for(int x = 0; x < this.getCount(); x++) {
            	r.add(getEl());
            }
            
            nodo.getRight().inOrderVisit(r);
        }

        
        /**
         * Trova il nodo dell'albero che contiene l'elemento successore di
         * quello contenuto in questo nodo. Il metodo assume che il nodo con un
         * elemento successore esista nell'albero. Ciò deve essere controllato
         * dal metodo getSuccessor() della classe principale.
         * 
         * @return un puntatore al nodo che contiene l'elemento successore a
         *         quello contenuto in questo nodo, secondo l'ordinamento
         *         naturale della classe E.
         */
        protected RBTreeNode getSuccessor() {
        	
        	if (!isNil(this.getRight())) {
        		
				/*
				 * Se il sottoalbero destro è diverso da null ritorno l'elemento minimo
				 * di cui questo è radice.
				 */
              	return this.getRight().getMinimum();
        	}
        	
        	RBTreeNode nodo = this;
        	
        	/*
        	 * Finche la condizione si avvera, assegno ad ognuna delle variabili locali i rispettivi
        	 * parent.
        	 */
        	while (nodo.equals(this.getParent().getRight())) {
                nodo = nodo.getParent();
                parent = this.getParent().getParent();
            }
            
        	/*
        	 * Ritorno il genitore del nodo.
        	 */
            return parent;
        }
        
        
        /**
         * Trova il nodo dell'albero che contiene l'elemento predecessore di
         * quello contenuto in questo nodo. Il metodo assume che il nodo con un
         * elemento predecessore esista nell'albero. Ciò deve essere controllato
         * dal metodo getPredecessor() della classe principale.
         * 
         * @return un puntatore al nodo che contiene l'elemento predecessore a
         *         quello contenuto in questo nodo, secondo l'ordinamento
         *         naturale della classe E.
         */
        protected RBTreeNode getPredecessor() {
        	
        	if (!isNil(this.getLeft())) {
        		
				/*
				 * Se il sottoalbero sinistro è diverso da null ritorno l'elemento
				 * massimo di cui questo è radice.
				 */
              	return this.getLeft().getMaximum();
        	}
        	
        	RBTreeNode nodo = this;
        	
        	while (nodo.equals(this.getParent().getLeft())) {
                nodo = nodo.getParent(); 
                parent = this.getParent().getParent();
            }
            
            return parent;
        }

        
        /**
         * Cerca un elemento nel (sotto)albero di cui questo nodo è radice.
         * 
         * @param el
         *               l'elemento da cercare
         * 
         * @return il puntatore al nodo che contiene l'elemento cercato oppure
         *         null se l'elemento non esiste nel (sotto)albero di cui questo
         *         nodo è la radice.
         */
        protected RBTreeNode search(E el) {

			RBTreeNode nodo = this;

			while (!isNil(nodo)) {

				int toCompare = el.compareTo(nodo.getEl());

				if (toCompare > 0) {
					nodo = nodo.getRight();

				} else if (toCompare < 0) {
					nodo = nodo.getLeft();

				} else {
					return nodo;
				}
				
			}

			/*
			 * Utilizzo il ciclo al fine di trovare l'elemento cercato nell' albero e restituisco il
			 * nodo se è stato trovato, null altrimenti.
			 */
			return null;
		}

        
        /**
         * Restituisce il nodo con elemento minimo nel (sotto)albero di cui
         * questo nodo è radice.
         * package it.unicam.cs.asdl1920.mp2;
         * @return il nodo con elemento minimo nel (sotto)albero di cui questo
         *         nodo è radice
         */
        protected RBTreeNode getMinimum() {
        	
        	RBTreeNode min = this;
        	
        	while (!isNil(min.getLeft())) {
        		min = min.getLeft();
        	}

        	return min;
        }
        
        
        /**
         * Restituisce il nodo con elemento massimo nel (sotto)albero di cui
         * questo nodo è radice.
         * 
         * @return il nodo con elemento massimo nel (sotto)albero di cui questo
         *         nodo è radice
         */
		protected RBTreeNode getMaximum() {

			RBTreeNode max = this;

			while (!isNil(max.getRight())) {
				max = max.getRight();
			}

			return max;
		}

		
        /**
         * @return the left
         */
        protected RBTreeNode getLeft() {
            return left;
        }
        
        
        /**
         * @return the el
         */
        protected E getEl() {
            return el;
        }

        
        /**
         * @return the right
         */
        protected RBTreeNode getRight() {
            return right;
        }
        
        
        /**
         * @return the color
         */
        protected boolean getColor() {
            return color;
        }
        
        
        /**
         * @return the parent
         */
        protected RBTreeNode getParent() {
            return parent;
        }
        
       
        /**
         * @return the count
         */
        protected int getCount() {
            return count;
        }
        
        
        /**
         * Determina se questo nodo è attualmente colorato di rosso.
         * 
         * @return true se questo nodo è attualmente colorato di rosso, false
         *         altrimenti
         */
        protected boolean isRed() {
            return this.color == RED;
        }
        
        
        /**
         * Determina se questo nodo è attualmente colorato di nero.
         * 
         * @return true se questo nodo è attualmente colorato di nero, false
         *         altrimenti
         */
        protected boolean isBlack() {
            return this.color == BLACK;
        }

        
        /**
         * Determina l'altezza nera di questo nodo, cioè il numero di nodi neri
         * che si incontrano in un qualsiasi cammino da qui ad una foglia NIL.
         * Non viene contata la colorazione di questo nodo.
         * 
         * @return l'altezza nera di questo nodo
         * 
         */
        protected int getBlackHeight() {
        	
        	RBTreeNode nodo = this;
        	
        	if (isNil(nodo)) {
        		return 0;
        	}
        	
    		int blackLeft = nodo.getLeft().getBlackHeight();
    		int blackRight = nodo.getRight().getBlackHeight();
    		
    		if (blackLeft != blackRight) {
    			return -1;
    		}
    		
    		return (nodo.color == RED) ? blackLeft : blackLeft + 1;
        }
        
        
        /**
         * Inserisce un elemento nel (sotto)albero di cui questo nodo è radice.
         * Se l'elemento è già presente viene semplicemente incrementata la sua
         * molteplicità.
         * 
         * @param el
         *               l'elemento da inserire
         * 
         * @return il numero di operazioni di comparazione (chiamate al metodo
         *         compareTo della classe E) effettuate per l'inserimento
         *         dell'elemento.
         */
		protected int insert(E el) {

			RBTreeNode x = this;
			RBTreeNode y = sentinella;
			int cmp = 0;

			while (!isNil(x)) {
				
				int tmp = x.getEl().compareTo(el);
				y = x;
				cmp++;

				if (tmp > 0) {
					x = x.getLeft();
					
				} else if (tmp < 0) {
					x = x.getRight();
					
				} else {
					getNodeOf(el).count++;
					size++;
					return 1;
				}
			}

			RBTreeNode z = new RBTreeNode(el);
			
			if (isNil(y)) {
				root = new RBTreeNode(el);
				size = numberOfNodes = 1;
				
			} else {
				
				if (y.getEl().compareTo(el) > 0) {
					y.left = z;
					y.setLeft(z);
					
				} else {
					y.right = z;
					y.setRight(z);
				}
				
				numberOfNodes++;
				size++;
				cmp++;
			}
			
			RBInsertFixup(z);
			return cmp;
		}
		

		private void RBInsertFixup(RBTreeNode z) {

			while (z.getParent().color == RED) {

				RBTreeNode nonno = z.getParent().getParent();

				if (z.getParent() == nonno.getLeft()) {
					RBTreeNode zioRight = nonno.getRight();

					/*
					 * Caso 1, se zio e nonno sono entrambi rossi, ricoloro entrambi di nero.
					 */
					if (zioRight.color == RED) {
						z.parent.color = BLACK;
						zioRight.color = BLACK;
						nonno.color = RED;

					} else {
						
						/*
						 * Caso 2/3, lo zio è nero ed eseguo le dovute rotazioni.
						 */
						if (z == z.getParent().getRight()) {
							
							/*
							 * Caso 2, ruoto a sinistra.
							 */
							z = z.getParent();
							leftRotate(z);
							z.parent = z.getParent();
						}

						/*
						 * Caso 3
						 */
						z.parent.color = BLACK;
						nonno.color = RED;
						rightRotate(nonno);
					}

				} else if (z.getParent() == nonno.getRight()) {

					RBTreeNode zioLeft = nonno.getLeft();

					/*
					 * Caso 1, zio e parent sono entrambi rossi, ricoloro entrambi di nero.
					 */
					if (zioLeft.color == RED) {
						zioLeft.color = BLACK;
						z.parent.color = BLACK;
						nonno.color = RED;

						/*
						 * Il nonno viene ricolorato di rosso.
						 */
						z = nonno;

					} else {
						/*
						 * Caso 2, ruoto a destra.
						 */
						if (z == z.getParent().getLeft()) {
							z = z.getParent();
							rightRotate(z);
							z.parent = z.getParent();
						}

						/*
						 * Caso 3
						 */
						nonno.color = RED;
						z.parent.color = BLACK;
						leftRotate(nonno);
					}
				}

			}

			/*
			 * Colorare root di nero nel caso fosse stato colorato di rosso durante la
			 * correzione.
			 */
			root.color = BLACK;
		}

		
        /**
         * Effettua una rotazione a sinistra.
         * 
         * @param x
         *              il nodo su cui effettuare la rotazione, deve avere un
         *              figlio destro
         * @throws IllegalArgumentException
         *                                      se il nodo passato ha figlio
         *                                      destro NIL
         */
        protected void leftRotate(RBTreeNode x) {

			if (isNil(x.getRight())) {
				throw new IllegalArgumentException();
			}
        	
            if (!isNil(x.getParent())) {
            	
                if (x == x.getParent().getLeft()) {
                    x.parent.left = x.getRight();
                    
                } else {
                    x.parent.right = x.getRight();
                }
                
                x.right.parent = x.getParent();
                x.parent = x.getRight();

                if (!isNil(x.right.left)) {
                    x.right.left.parent = x;
                }
                
                x.right = x.getRight().getLeft();
                x.parent.left = x;

            } else {

            	/*
            	 * Nel caso non si avvera la prima condizione, bisogna ruotare la radice.
            	 */
                RBTreeNode right = this.getRight();
                right.left.parent = this;
                right.parent = sentinella;

                this.parent = right;
                this.right = right.getLeft();
                this.parent.left = root;
                root = right;
            }
		}
        
        
        /**
         * Effettua una rotazione a destra.
         * 
         * @param x
         *              il nodo su cui effettuare la rotazione, deve avere un
         *              figlio sinistro
         * 
         * @throws IllegalArgumentException
         *                                      se il nodo passato ha figlio
         *                                      sinistro NIL
         */
		protected void rightRotate(RBTreeNode x) {

			if (isNil(x.getLeft())) {
				throw new IllegalArgumentException();
			}

	        if (!isNil(x.getParent())) {
	        	
	            if (x == x.getParent().getLeft()) {
	                x.parent.left = x.getLeft();
	                
	            } else {
	                x.parent.right = x.getLeft();
	            }

	            x.left.parent = x.getParent();
	            x.parent = x.getLeft();
	            
	            if (!isNil(x.getLeft().getRight())) {
	                x.left.right.parent = x;
	            }
	            
	            x.left = x.getLeft().getRight();
	            x.parent.right = x;
	            
	        } else {
	        	
	            RBTreeNode left = root.getLeft();
	            left.right.parent = this;
	            left.parent = sentinella;

	            root.parent = left;
	            root.left = this.getLeft().getRight();
	            left.right = root;
	            root = left;
	        }
		}
		
		
        /**
         * Rimuove un elemento dal (sotto)albero di cui questo nodo è radice. Se
         * l'elemento ha molteplicità maggiore di 1 allora viene semplicemente
         * decrementata la molteplicità.
         * 
         * @param el
         *               l'elemento da rimuovere
         * 
         * @return true se l'elemento è stato rimosso (anche solo semplicemente
         *         decrementando la molteplicità), false se non era presente.
         */
		protected boolean remove(E el) {

			RBTreeNode y = sentinella;
			RBTreeNode nodeEl = getNodeOf(el);

			if (getNodeOf(el).getCount() > 1) {
				getNodeOf(el).count--;
				size--;
				return true;
			}

			if (size == 1) {
				root = sentinella;
				size--;
				numberOfNodes--;
				return true;
			}

			if (isNil(nodeEl.getRight()) ) {
				y = nodeEl;
				
			} else if (isNil(nodeEl.getLeft())) {
				y = nodeEl;
				
			} else {
				y = nodeEl.getSuccessor();
			}
			
			RBTreeNode z = null;
			
			if (isNil(y.getLeft())) {
				z = y.getRight();
				
			} else {
				z = y.getLeft();
			}
			
			z.parent = y.getParent();

			if (!(isNil(y.getParent()))) {
				
				if (y == y.getParent().getLeft()) {
					y.parent.left = z;
				} else if (y == y.getParent().getRight()) {
					y.parent.right = z;
				}
				
			} else {
				root = z;
			}

			if (y != nodeEl) {
				
				y.color = nodeEl.getColor();
				
				y.parent = nodeEl.getParent();
				y.setParent(nodeEl);
				
				y.right = nodeEl.getRight();
				y.setRight(nodeEl.getRight());
				
				y.left = nodeEl.getLeft();
				y.setLeft(nodeEl.getLeft());
			}

			if (isNil(y.getParent())) {
				root = y;
			}

			if (root.getBlackHeight() == -1) {
				removeFixUp(z);
			}

			numberOfNodes--;
			size--;
			return true;
		}

		
		private void removeFixUp(RBTreeNode x) {
			
			RBTreeNode y = sentinella;
			
			while (x != root && x.color == BLACK) {
				
				if (x == x.getParent().getLeft()) {
					y = x.getParent().getRight();
					
					if (y.color == RED) {
						
						/*
						 * Caso 1, il fratello di y è rosso.
						 */
						x.parent.color = RED;
						y.color = BLACK;
						leftRotate(x.getParent());
						y = x.getParent().getRight();
					}
					
					if (y.getRight().color == BLACK) {
						
						/*
						 * Se il fratello di x è nero ma ha figli rossi.
						 */
						y.color = RED;
						x = x.getParent();
					}
					
				} else {
					
					/*
					 * Caso 3, se il fratello di x è nero, ma ha figli di colore
					 * diverso.
					 */
					y = x.getParent().getLeft();
					
					if (y.isRed()) {
						x.parent.color = RED;
						y.color = BLACK;
						rightRotate(x.getParent());
						y = x.getParent().getLeft();
					}
					
					if (y.getRight().color == BLACK) {
						y.color = RED;
						x = x.getParent();
						
					} else {
						
						if (y.getLeft().color == BLACK) {
							leftRotate(y);
						}
						
						rightRotate(x.getParent());
						x = root;
					}
				}
				
			}
			
			x.color = BLACK;
		}
		
		
        private void setParent(RBTreeNode parent) {
			if (parent == parent.getParent().getLeft()) {
				parent.parent.left = this;
			} else {
				parent.parent.right = this;
			}
		}
        
        private void setRight(RBTreeNode dx) {
        	dx.parent = this; 
        }
		
        private void setLeft(RBTreeNode sx) {
        	sx.parent = this; 
        }
        
	}
}
