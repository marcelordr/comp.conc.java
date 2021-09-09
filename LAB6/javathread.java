/* Disciplina: Computacao Concorrente */
/* Prof.: Silvana Rossetto */
/* Laboratório: 6 */
/* Aluno: Marcelo Augustos J Rodrigues */
/* DRE: 118143203 */
/* -------------------------------------------------------------------*/

class  thread extends Thread{

    int id; // identificador das threads
    int bloco = javathread.tamvetor/javathread.Nthreads; 

    //Construtor da thread
    public thread(int tid){
        this.id = tid;
    }

    
    public synchronized void par()
    {
        javathread.npar++;
    }

    public void run() //tarefa das threads
    {
        if(this.id==javathread.Nthreads-1){
            for(int i=id*bloco;i < javathread.tamvetor;i++){
                if(javathread.vetor[i]%2==0){
                    par();
                }
            }
        }
        else{
            for(int i=id*bloco ; i<bloco*(this.id+1) ; i++){
                if(javathread.vetor[i]%2==0){
                    par();
                }
            }
        }
    }
}

public class javathread {
    
    static int Nthreads = 10;  // quantidade de threads usadas << 
    static int tamvetor = 500;  // tamanho do vetor <<
    static public int npar = 0;  // variavel que tera a quantidade final de pares encontrados
    static public int[] vetor;

    public static void main (String[] args)
    {
        
        Thread[] threads = new Thread[Nthreads];
        vetor = new int[tamvetor];

        for (int i=0;i<tamvetor;i++) // percorrer o vetor
        {
            vetor[i]= i+1;
        }

        for (int i=0; i<threads.length; i++)  //criação das threads
        {
            threads[i] = new thread(i);
        }

        for (int i=0; i<threads.length; i++) // start das threads
        {
            threads[i].start();
        }
        
       for (int i=0; i<threads.length; i++) // esperando o termino das threads
        {
            try { threads[i].join(); } catch (InterruptedException e) 
            {
                System.out.println("ERRO");
            }
        }

        System.out.println("Quantidade de pares =  " + npar); 
    }
}
