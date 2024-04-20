/*******************************************************************************
 * Copyright (c) 2001-2014 Yann-Gaël Guéhéneuc  and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Yann-Gaël Guéhéneuc  and others, see in file; API and its implementation
 ******************************************************************************/
package ptidej.solver;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;

import padl.kernel.IAbstractLevelModel;
import padl.kernel.IAbstractModel;
import padl.motif.IDesignMotifModel;
import padl.visitor.IGenerator;
import padl.visitor.IWalker;
import ptidej.solver.fingerprint.ReducedDomainBuilder;
import ptidej.solver.java.domain.Manager;
import util.io.Files;
import util.io.OutputMonitor;
import util.io.ProxyConsole;
import util.io.ProxyDisk;
import util.io.ReaderInputStream;
import util.multilingual.MultilingualManager;

/**
 * SolutionGenerator is a unique class (Singleton)
 * which provides a unique API to call the constraint
 * solver and get a Properties file containing the
 * solutions generated by the constraint solver.
 * The location of the constraint solver and of the
 * result file are obtained from the PropertyManager
 * class.
 */
public final class OccurrenceGenerator {
	public static final int PROBLEM_AC4 = 0;
	public static final int PROBLEM_CUSTOM = 1;
	public static final int SOLVER_AUTOMATIC = 2;
	public static final int SOLVER_COMBINATORIAL_AUTOMATIC = 3;
	public static final int SOLVER_SIMPLE_AUTOMATIC = 4;
	// TODO: Using constant passed in parameter is bad programming
	// bacause it bypasses the type system. I should remove the
	// following constants and offer different getSolutions() methods.
	public static final int VERSION_METRICAL_PTIDEJSOLVER4 = 5;
	public static final int VERSION_PTIDEJSOLVER2 = 6;
	public static final int VERSION_PTIDEJSOLVER3 = 7;
	public static final int VERSION_PTIDEJSOLVER4 = 8;

	private static OccurrenceGenerator UniqueInstance;

	public static OccurrenceGenerator getInstance() {
		if (OccurrenceGenerator.UniqueInstance == null) {
			OccurrenceGenerator.UniqueInstance = new OccurrenceGenerator();
		}
		return OccurrenceGenerator.UniqueInstance;
	}

	private Properties callClaireConstraintSolver() {
		// Then, I call the constraint solver and read the results.
		final Properties properties = new Properties();
		try {
			// TODO: WARNING! The use of File.getClassPath() will
			// prevents the solver to be call from Eclipse...
			// Yann 2013/05/29: Path
			// I now use the ProxyDisk to handle directories and files
			// so I must ask it where is its temporary directory to use
			// this directory to locate the instructions...
			//	final String commandLine =
			//		('\"' + Files.getClassPath(OccurrenceGenerator.class)
			//				+ PropertyManager.getSolverDirectory()
			//				+ PropertyManager.getSolverCommand() + "\" "
			//				+ PropertyManager.getSolverArguments() + " -f \""
			//				+ PropertyManager.getSolverDomainDirectory() + "Instructions.cl\"")
			//			.replace('/', '\\');
			final String commandLine = ('\"'
					+ Files.getClassPath(OccurrenceGenerator.class)
					// Yann 2003/10/24: Relativity!
					// The PtidejSolver executable file must be
					// in the Ptidej project folder where it
					// will be found automatically, no need for
					// hard-coded path anymore!
					// return "C:/Documents and Settings/Yann/Work/Ptidej Solver 3/Ptidej/";
					+ "../PtidejSolver.exe\" -s 4 4" + " -f \""
					+ System.getProperty("user.dir") + '/'
					+ ProxyDisk.getInstance().directoryTempString()
					+ "Instructions.cl\"").replace('/', '\\');
			final Process process = Runtime.getRuntime().exec(commandLine);
			final OutputMonitor errorStreamMonitor = new OutputMonitor(
					"Ptidej Solver Error Stream Monitor", "",
					process.getErrorStream(), System.out);
			errorStreamMonitor.start();
			final OutputMonitor inputStreamMonitor = new OutputMonitor(
					"Ptidej Solver Input Stream Monitor", "",
					process.getInputStream(), System.out);
			inputStreamMonitor.start();

			// I wait for the process to finish; i.e., I wait for
			// the constraint solver to be done solving the constraints.
			try {
				process.waitFor();
			}
			catch (final InterruptedException ie) {
				ie.printStackTrace(ProxyConsole.getInstance().errorOutput());
			}

			// I check if everything went alright.
			if (process.exitValue() != 0) {
				while (process.getErrorStream().available() > 0) {
					System.out.print((char) process.getErrorStream().read());
				}
			}

			// I load the results.
			properties.load(new FileInputStream(
					ProxyDisk.getInstance().directoryTempString()
							+ "ConstraintResults.ini"));
		}
		catch (final IOException ioe) {
			ioe.printStackTrace(ProxyConsole.getInstance().errorOutput());
		}

		return properties;
	}

	private Properties callJavaConstraintSolver(
			final ptidej.solver.java.Problem problem, final char[] motifName,
			final IAbstractModel source, final int solver) {

		final Properties solutions = new Properties();

		try {
			final String resultFile = "ConstraintResults.ini";
			final Writer writer = ProxyDisk.getInstance()
					.fileTempOutput(resultFile);

			problem.setWriter(new PrintWriter(writer));
			if (solver == OccurrenceGenerator.SOLVER_AUTOMATIC) {
				problem.automaticSolve(true);
			}
			else if (solver == OccurrenceGenerator.SOLVER_COMBINATORIAL_AUTOMATIC) {
				problem.combinatorialAutomaticSolve(true);
			}
			else if (solver == OccurrenceGenerator.SOLVER_SIMPLE_AUTOMATIC) {
				problem.simpleAutomaticSolve(true);
			}

			solutions.load(new ReaderInputStream(
					ProxyDisk.getInstance().fileTempInput(resultFile)));
		}
		catch (final IOException e) {
			e.printStackTrace(ProxyConsole.getInstance().errorOutput());
		}

		return solutions;
	}

	private Properties callPtidejSolver4(final char[] motifName,
			final IAbstractModel source, final int solver) {

		try {
			final StringBuffer buffer = new StringBuffer();
			buffer.append("ptidej.solver.problem.");
			buffer.append(motifName);
			buffer.append("Motif");
			final Class<?> chosenPattern = Class.forName(buffer.toString());
			final Method problemMethod = chosenPattern.getDeclaredMethod(
					"getProblem", new Class[] { List.class });
			final ptidej.solver.java.Problem problem = (ptidej.solver.java.Problem) problemMethod
					.invoke(null, new Object[] {
							Manager.build((IAbstractLevelModel) source) });

			return this.callJavaConstraintSolver(problem, motifName, source,
					solver);
		}
		catch (final IllegalArgumentException e) {
			e.printStackTrace(ProxyConsole.getInstance().errorOutput());
		}
		catch (final ClassNotFoundException e) {
			e.printStackTrace(ProxyConsole.getInstance().errorOutput());
		}
		catch (final NoSuchMethodException e) {
			e.printStackTrace(ProxyConsole.getInstance().errorOutput());
		}
		catch (final IllegalAccessException e) {
			e.printStackTrace(ProxyConsole.getInstance().errorOutput());
		}
		catch (final InvocationTargetException e) {
			e.printStackTrace(ProxyConsole.getInstance().errorOutput());
		}

		return new Properties();
	}

	private Properties callPtidejSolver4Metrical(final char[] motifName,
			final IAbstractModel source, final int solver) {

		try {
			final StringBuffer buffer = new StringBuffer();
			buffer.append("ptidej.solver.fingerprint.problem.");
			buffer.append(motifName);
			buffer.append("Motif");
			final Class<?> chosenPattern = Class.forName(buffer.toString());
			final Method problemMethod = chosenPattern.getDeclaredMethod(
					"getProblem",
					new Class[] { List.class, ReducedDomainBuilder.class });
			// Yann 2004/12/03: Consistency.
			// Manger and ReducedDomainBuilder take different type
			// of parameters as arguments, these should be consistent!
			final ptidej.solver.fingerprint.Problem problem = (ptidej.solver.fingerprint.Problem) problemMethod
					.invoke(null,
							new Object[] {
									Manager.build((IAbstractLevelModel) source),
									new ReducedDomainBuilder(
											(IAbstractLevelModel) source) });

			return this.callJavaConstraintSolver(problem, motifName, source,
					solver);
		}
		catch (final IllegalArgumentException e) {
			e.printStackTrace(ProxyConsole.getInstance().errorOutput());
		}
		catch (final ClassNotFoundException e) {
			e.printStackTrace(ProxyConsole.getInstance().errorOutput());
		}
		catch (final NoSuchMethodException e) {
			e.printStackTrace(ProxyConsole.getInstance().errorOutput());
		}
		catch (final IllegalAccessException e) {
			e.printStackTrace(ProxyConsole.getInstance().errorOutput());
		}
		catch (final InvocationTargetException e) {
			e.printStackTrace(ProxyConsole.getInstance().errorOutput());
		}

		return new Properties();
	}

	private void createClaireConstraints(final IDesignMotifModel model,
			final IGenerator constraintGenerator) {

		if (model != null) {
			try {
				model.generate(constraintGenerator);

				final Writer writer = ProxyDisk.getInstance()
						.fileTempOutput(model.getDisplayName() + "Pattern.cl");
				writer.write(constraintGenerator.getCode());
				writer.close();
			}
			catch (final IOException ioe) {
				ioe.printStackTrace(ProxyConsole.getInstance().errorOutput());
			}
		}
	}

	private void createClaireDomain(final IAbstractModel source,
			final IWalker domainGenerator) {
		try {
			source.walk(domainGenerator);

			final Writer writer = ProxyDisk.getInstance()
					.fileTempOutput("Domain.cl");
			writer.write(domainGenerator.getResult().toString());
			writer.close();
		}
		catch (final IOException ioe) {
			ioe.printStackTrace(ProxyConsole.getInstance().errorOutput());
		}
	}

	private void createClaireInstructions(final IDesignMotifModel pattern,
			final String solver, final String problem) {

		Writer writer = null;
		try {
			writer = ProxyDisk.getInstance().fileTempOutput("Instructions.cl");

			final StringBuffer instructions = new StringBuffer();
			instructions.append("[solvePtidejProblem() : void\n");
			instructions.append("\t->\tPtidejResultFile := \"");
			instructions.append(ProxyDisk.getInstance().directoryTempString());
			instructions.append("ConstraintResults.ini");
			instructions.append("\",\n\t\tprintHeader(),\n\t\tprintf(\"~A ");
			instructions.append("Loading domain file\t\", char!(179)),\n");
			if (pattern != null) {
				instructions.append("\t\tload(\"");
				// Yann 2013/05/29: Path
				// I now use the ProxyDisk to handle directories and files
				// so I must ask it where is its temporary directory to use
				// this directory to locate the instructions...
				//	instructions.append(PropertyManager.getSolverDomainDirectory());
				instructions
						.append(ProxyDisk.getInstance().directoryTempString());
				instructions.append(pattern.getName());
				instructions.append("Pattern.cl");
				instructions.append("\"),\n");
			}
			instructions.append("\t\tload(\"");
			// Yann 2013/05/29: Path
			// I now use the ProxyDisk to handle directories and files
			// so I must ask it where is its temporary directory to use
			// this directory to locate the instructions...
			//	instructions.append(PropertyManager.getSolverDomainDirectory());
			instructions.append(ProxyDisk.getInstance().directoryTempString());
			instructions.append("Domain.cl");
			instructions.append("\"),\n\t\tsearchConcretePatterns(\n\t\t\t");
			instructions.append(solver);
			instructions.append(" @ PtidejProblem,\n\t\t\t");
			instructions.append(problem);
			instructions.append("Pattern()),\n\t\texit(-1)\n]\n\n");
			instructions.append("(solvePtidejProblem())");
			writer.write(instructions.toString());
		}
		catch (final Exception e) {
			e.printStackTrace(ProxyConsole.getInstance().errorOutput());
		}
		finally {
			if (writer != null) {
				try {
					writer.close();
				}
				catch (final Exception e) {
					e.printStackTrace(ProxyConsole.getInstance().errorOutput());
				}
			}
		}
	}

	// Yann 2004/12/03: Duplicate for the cause!
	// Methods generatePtidejSolver2ExecutionData() and
	// generatePtidejSolver3ExecutionData() are almost
	// duplicate but I don't know if it is worth abstracting
	// them because they don't do much and won't change much.
	public void generatePtidejSolver2ExecutionData(final char[] motifName,
			final IDesignMotifModel pattern, final IAbstractModel source,
			final int solver, final int problem) {

		ptidej.solver.claire.OccurrenceGenerator.getInstance()
				.generatePtidejSolver2ExecutionData(motifName, pattern, source,
						solver, problem);
	}

	public void generatePtidejSolver3ExecutionData(final char[] aMotifName,
			final IDesignMotifModel aMotifModel,
			final IAbstractModel aSourceModel, final int aSolverKind,
			final int aProblemKind) {

		ptidej.solver.claire.OccurrenceGenerator.getInstance()
				.generatePtidejSolver3ExecutionData(aMotifName, aMotifModel,
						aSourceModel, aSolverKind, aProblemKind);

	}

	public Properties getOccurrences(final IDesignMotifModel aMotif,
			final IAbstractModel source, final int version, final int solver,
			final int problem) {

		return this.getOccurrences(aMotif.getName(), aMotif, source, version,
				solver, problem);
	}

	public Properties getOccurrences(final char[] motifName,
			final IAbstractModel source, final int version, final int solver,
			final int problem) {

		return this.getOccurrences(motifName, null, source, version, solver,
				problem);
	}

	private Properties getOccurrences(final char[] motifName,
			final IDesignMotifModel motif, final IAbstractModel source,
			final int version, final int solver, final int problem) {

		switch (version) {
		case OccurrenceGenerator.VERSION_PTIDEJSOLVER2:
			this.generatePtidejSolver2ExecutionData(motifName, motif, source,
					solver, problem);

			// Fourth, I call the constraint solver.
			return this.callClaireConstraintSolver();

		case OccurrenceGenerator.VERSION_PTIDEJSOLVER3:
			this.generatePtidejSolver3ExecutionData(motifName, motif, source,
					solver, problem);

			// Fourth, I call the constraint solver.
			return this.callClaireConstraintSolver();

		case OccurrenceGenerator.VERSION_PTIDEJSOLVER4:
			return this.callPtidejSolver4(motifName, source, solver);

		case OccurrenceGenerator.VERSION_METRICAL_PTIDEJSOLVER4:
			return this.callPtidejSolver4Metrical(motifName, source, solver);

		default:
			throw new RuntimeException(
					new IllegalAccessException(MultilingualManager.getString(
							"UNKNOWN_SOLVER_VER", OccurrenceGenerator.class)));
		}
	}
}
