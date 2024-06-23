package org.evd.BootStrap.config;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class NodeConfig {
    private List<NodeInfo> nodes = new ArrayList<>();

    public static NodeConfig load(String filePath) {
        Yaml yaml = new Yaml();
        try (InputStream in = new FileInputStream(filePath)){
            return yaml.loadAs(in, NodeConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<NodeInfo> getNodes() {
        return nodes;
    }

    public void setNodes(List<NodeInfo> nodes) {
        this.nodes = nodes;
    }

}
