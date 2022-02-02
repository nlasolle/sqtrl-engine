package org.ahp.sqtrlengine.evaluation;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Export the evaluation results to csv file
 * A row corresponds to a combination of parameters
 * @author Nicolas Lasolle
 *
 */
public class ResultsExporter {

	public void export(List<CombinationResult> results) throws FileNotFoundException {
		List<String[]> data = new ArrayList<>();
		
		//Construct the header (both parameter and results are saved in the csv file)
		String [] header = {
				"Endpoint",
				"Max cost",
				"Pruning?",
				"Rules",
				"Dataset",
				"Query",
				"Average query generation time",
				"Full tree exploration time",
				"Generated queries"
		};
		
		data.add(header);
		
		for(CombinationResult result : results) {
			String [] dataLine = {
					result.getParameterCombination().isLocalEndpoint() ? "local" : "distant", 
					Double.toString(result.getParameterCombination().getMaxCost()),
					result.getParameterCombination().isPruning() ? "Yes" : "No", 
					Integer.toString(result.getParameterCombination().getRules().size()),
					result.getParameterCombination().getDataset(),
					result.getParameterCombination().getQuery(),
					Double.toString(result.getAverageRuleApplicationTime()),
					Double.toString(result.getFullTreeTime()),
					Integer.toString(result.getGeneratedQueries())	
			};
			
			 data.add(dataLine);
		}
		String fileName = "sqtrl_evaluation_" + new SimpleDateFormat("yyyyMMddHHmmss' .csv'").format(new Date());

		File csvOutputFile = new File(fileName);
		try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
			data.stream()
			.map(this::convertToCSV)
			.forEach(pw::println);
		}
	}

	public String convertToCSV(String[] data) {
		return Stream.of(data)
				.map(this::escapeSpecialCharacters)
				.collect(Collectors.joining(","));
	}

	public String escapeSpecialCharacters(String data) {
		String escapedData = data.replaceAll("\\R", " ");
		if (data.contains(",") || data.contains("\"") || data.contains("'")) {
			data = data.replace("\"", "\"\"");
			escapedData = "\"" + data + "\"";
		}
		return escapedData;
	}

}
