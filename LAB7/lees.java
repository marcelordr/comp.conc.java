/* Disciplina: Computacao Concorrente */
/* Prof.: Silvana Rossetto */
/* Laboratório: 6 */
/* Aluno: Marcelo Augustos J Rodrigues */
/* DRE: 118143203 */
/* -------------------------------------------------------------------*/

import java.lang.Math;
import java.util.Scanner;

class LeEs {
    private int leit, escr;  
    
    // Construtor da classe
    LeEs() { 
       this.leit = 0; //leitores lendo
       this.escr = 0; //escritor escrevendo (máx 1)
    } 
    
    // Entrada para leitores
    public synchronized void EntraLeitor (int id,String type) {
      try { 
        while (this.escr > 0) {
           System.out.println (type+ " " +id+ " bloqueado");
           wait();  //bloqueia pela condicao logica da aplicacao 
        }
        this.leit++;  //registra que ha mais um leitor lendo
        System.out.println (type+ " " +id+ " lendo");
      } catch (InterruptedException e) { }
    }
    
    // Saida para leitores
    public synchronized void SaiLeitor (int id,String type) {
       this.leit--; //registra que um leitor saiu
       if (this.leit == 0) 
             this.notify(); //libera escritor (caso exista escritor bloqueado)
       System.out.println (type+ " " +id+ " saindo");
    }
    
    // Entrada para escritores
    public synchronized void EntraEscritor (int id, String type) {
      try { 
        while ((this.leit > 0) || (this.escr > 0)) {
           System.out.println (type+ " " +id+ " bloqueado");
           wait();  //bloqueia pela condicao logica da aplicacao 
        }
        this.escr++; //registra que ha um escritor escrevendo
        System.out.println (type+ " " +id+ " escrevendo");
      } catch (InterruptedException e) { }
    }
    
    // Saida para escritores
    public synchronized void SaiEscritor (int id , String type) {
       this.escr--; //registra que o escritor saiu
       notifyAll(); //libera leitores e escritores (caso existam leitores ou escritores bloqueados)
       System.out.println (type+ " " +id+ " saindo");
    }
  }

// Leitor
class Le extends Thread {
  int id; //Identificador da Thread
  int delay; //Atraso
  LeEs monitor; //Objeto monitor para coordenar a lógica de execução das Thread
  String type = new String("Leitor"); //Identificador do tipo de Thread (leitor, escritor ou leitor escritor)
  // Construtor
  Le (int id, int delayTime, LeEs m) {
    this.id = id;
    this.delay = delayTime;
    this.monitor = m;
  }

  // Método que determina primalidade de um inteiro n
  private boolean Primo(int n){  
    if(n==1||n==0){return false;}
    for(int i=2;i<=n/2;i++){
      if(n%i==0){
        return false;
      }
    }
    return true;
  }


  // Método executado pela Thread
  public void run () {
    try {
      for (;;) {
        this.monitor.EntraLeitor(this.id,type);
        System.out.println("Leitor " +this.id+ " leu " +Monitor.v);
        if(Primo(Monitor.v)){
         System.out.println("Leitor " +this.id+ " viu que " +Monitor.v+ ": primo");
        }
        else{
          System.out.println("Leitor " +this.id+ " viu que " +Monitor.v+ ": não primo");
        }
        this.monitor.SaiLeitor(this.id,type);
        sleep(this.delay); 
      }
    } catch (InterruptedException e) { return; }
  }
}

// Escritor
class Escreve extends Thread {
  int id; //Identificador da Thread
  int delay; //Atraso
  LeEs monitor; //Objeto monitor para coordenar a lógica de execução das Thread
  String type = new String("Escritor"); //Identificador do tipo de Thread (leitor, escritor ou leitor escritor)

  // Construtor
  Escreve (int id, int delayTime, LeEs monitor) {
    this.id = id;
    this.delay = delayTime;
    this.monitor = monitor;
  }

  // Método executado pela Thread
  public void run () {
    try {
      for (;;) {
        this.monitor.EntraEscritor(this.id,type); 
        Monitor.v = this.id;
        System.out.println("Escritor " +this.id+ " escreveu " + Monitor.v);
        this.monitor.SaiEscritor(this.id,type); 
        sleep(this.delay);
      }
    } catch (InterruptedException e) { return; }
  }
}

//LeitorEscritor
class Leesc extends Thread{
  
  int id; //Identificador da Thread
  int delay; //Atraso
  LeEs monitor; //Objeto monitor para coordenar a lógica de execução das Thread
  String type = new String("Leitor Escritor"); //Identificador do tipo de Thread (leitor, escritor ou leitor escritor)

  // Construtor
  Leesc (int id, int delayTime, LeEs monitor) {
    this.id = id;
    this.delay = delayTime;
    this.monitor = monitor;
  }

  // Método para determinar se um inteiro n é par ou impar
  private void parImpar(int n){
    if(n%2==0){
      System.out.println("Leitor Escritor viu que " +n+ ": par ");
    }
    else{
      System.out.println("Leitor Escritor viu que " +n+ ": impar");
    }

  }
  // Método executado pela Thread
  public void run () {
    try {
      for (;;) {
        this.monitor.EntraLeitor(this.id,type);
        System.out.println("Leitor Escritor " +this.id+ " leu " +Monitor.v);
        parImpar(Monitor.v);
        this.monitor.SaiLeitor(this.id,type);
        this.monitor.EntraEscritor(this.id,type);
        Monitor.v=2*Monitor.v; 
        System.out.println("Leitor Escritor " +this.id+ " escreveu " +Monitor.v );
        this.monitor.SaiEscritor(this.id,type); 
        sleep(this.delay);
      }
    } catch (InterruptedException e) { return; }
  }
}

class Monitor {
    public static int v = 0; // Variável global
    static final int R = 4;  // Quantidade de leitores
    static final int W = 3;  // Quantidade de escritores
    static final int WR = 2; // Quantidade de leitores e escritores

  public static void main(String[] args) {
    int i;
    LeEs monitor = new LeEs();    // Monitor
    Le[] r = new Le[R];           // Thread leitores
    Escreve[] w = new Escreve[W]; // Thread escritores
    Leesc[] wr = new Leesc[WR];   // Thread le/esc
    
    for (i=0; i<R; i++) {
      r[i] = new Le(i+1, (i+1)*500, monitor); 
      r[i].start(); 
    }

   for (i=0; i<W; i++) {
      w[i] = new Escreve(i+1, (i+1)*500, monitor); 
      w[i].start(); 
    }

   for (i=0; i<WR; i++) {
    wr[i] = new Leesc(i+1, (i+1)*500, monitor); 
    wr[i].start(); 
    }
  }
}
