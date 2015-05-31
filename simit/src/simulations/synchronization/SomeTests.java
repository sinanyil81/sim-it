package simulations.synchronization;

import simit.statistics.GaussianDistribution;
import simulations.shared.Logger;

public class SomeTests {

	public static void main(String[] args) {
		//gradientDescentAlgorithm();
		asymptoticVariance();
	}

	static void gradientDescentAlgorithm() {
		Logger logger = new Logger("gradient.txt");
		double f = 1000000.0;
		double B = 30;

		double f_i = 1000100.0;

		double delta_i = 1.0;
		double alpha_i = 1.0;
		double error_i = 1.0;

		double delta_j = 1.0;
		double alpha_j = 0.5;
		double error_j = 1.0;

		double delta_k = 1.0;
		double alpha_k = 0.1;
		double error_k = 1.0;

		double delta_l = 1.0;
		double alpha_l = 1.0;
		double error_l = 1.0;
		double lastDerivative = 0.0;

		logger.log(new String("" + 0 + " " + delta_i * f_i + " " + delta_j
				* f_i + " " + delta_k * f_i + " " + delta_l * f_i));

		for (int i = 1; i < 100; i++) {
			double delay = GaussianDistribution.nextGaussian(0, 100);

			error_i = (delta_i * f_i - f);
			error_i *= B;
			error_i += delay;
			delta_i -= alpha_i * error_i / (B * f);

			error_j = (delta_j * f_i - f);
			error_j *= B;
			error_j += delay;
			delta_j -= alpha_j * error_j / (B * f);

			error_k = (delta_k * f_i - f);
			error_k *= B;
			error_k += delay;
			delta_k -= alpha_k * error_k / (B * f);

			error_l = (delta_l * f_i - f);
			double derivative = error_l;
			error_l *= B;
			error_l += delay;

			if (Math.signum(derivative) == Math.signum(lastDerivative)) {
				alpha_l *= 2.0f;
			} else {
				alpha_l /= 3.0f;
			}

			if (alpha_l > 1.0f)
				alpha_l = 1.0f;

			// double delay = GaussianDistribution.nextGaussian(0, 100);
			// error += delay;

			delta_l -= alpha_l * error_l / (B * f);
			lastDerivative = derivative;

			logger.log(new String("" + i + " " + delta_i * f_i + " " + delta_j
					* f_i + " " + delta_k * f_i + " " + delta_l * f_i));

			if (i == 20)
				f_i = 1000050.0;
		}
		logger.close();
	}
	
	static void asymptoticVariance() {
		Logger logger = new Logger("asymptotic.txt");
		
		double f = 1000000.0;
		double B = 30000000;
		
		double sigmasquare_w = 1/100000000.0;
		double sigmasquare_v = 5/10000.0;

		double nsquare_w = sigmasquare_w/f;
		double nsquare_t = sigmasquare_v*f; 
		
		double alpha = 0.0;
		
		for (int i = 0; i < 20; i++) {
			
			double error = alpha*B*B*((3-alpha*f*B)*nsquare_t + 2*nsquare_w*f*f*B*B)*(1+nsquare_w);
			error /= 2*B*f - alpha*B*B*f*f*(1+nsquare_w);
			error += nsquare_t/(f*f);
			error += 2*nsquare_w*B*B;
			error = Math.sqrt(error);
			logger.log(""+alpha+" "+error*10000.0);
			alpha += 0.1;
		}
		logger.close();
	}

}
