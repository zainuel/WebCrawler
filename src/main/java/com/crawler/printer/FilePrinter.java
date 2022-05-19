package com.crawler.printer;

import com.crawler.model.Graph;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;

@Slf4j
public class FilePrinter implements GraphPrinter {


    @Override
    public void print(Graph graph) {

        Optional<PrintWriter> optionalWriter = getWriter();
        if (!optionalWriter.isPresent()) return;
        PrintWriter writer = optionalWriter.get();
        writer.write("<-------------------------Writing graph output to output.txt file---------------------------->\n");
        writer.write("Total nodes " + graph.getNodeToEdgesMap().size() + "\n");

        for (URL url : graph.getNodeToEdgesMap().keySet()) {
            writer.write("Url  ------> " + url + "\n");
            writer.write("Number of Linked Urls found -----> " + graph.get(url).size() + "\n");

            for (URL linkedUrl : graph.get(url)) {
                writer.write(" Linked Url  ---------> " + linkedUrl + "\n");
            }
        }

        writer.write("<-------------------------Printing Complete---------------------------->");
        writer.close();
    }

    private Optional<PrintWriter> getWriter() {
        try {
            Files.deleteIfExists(new File("output.txt").toPath());
            FileWriter fileWriter = new FileWriter("output.txt");
            return Optional.of(new PrintWriter(fileWriter, true));
        } catch (IOException e) {
            log.error("Error while creating output file {}", e);
        }

        return Optional.empty();
    }
}
