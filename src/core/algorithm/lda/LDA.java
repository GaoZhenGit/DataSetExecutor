package core.algorithm.lda;




public class LDA implements Runnable{

	@Override
	public void run() {
		LDAOption option = new LDAOption();
		option.dir = "./result";
		option.dfile = "doc.dat";
		option.est = true;  /////
		option.estc = true;
		///option.estc = true;
		option.inf = false;
		option.modelName = "model-final";
		option.niters = 100;
		option.savestep = 1000;
		option.K = 4;
		Estimator estimator = new Estimator();
		estimator.init(option);
		estimator.estimate();
	}

	public static void main(String[] args) {
		new LDA().run();
	}
	
}
