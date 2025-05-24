package com.example.demo.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.stream.ByteCapacity;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.Producer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RabbitStreamStarter {
    RabbitStreamStarter() {
        Thread t = new SendThread();
        t.start();
    }
}

class SendThread extends Thread {
    @Override
    public void run() {
        Environment environment = Environment.builder().build();
        String stream = "Server2Runner";
        environment.streamCreator().stream(stream).maxLengthBytes(ByteCapacity.GB(1)).create();
        Producer producer = environment.producerBuilder().stream(stream).build();
        FormData formData = new FormData(List.of(new CMD(
                List.of("--version"), "gcc", new Config(
                1, 1, 256000, 4096000, false, 0, 0
        ), ""
        )), "gcc:14.2", "xxxx-xxxx");
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        String message = null;
        try {
            message = mapper.writeValueAsString(formData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println(message);
        producer.send(producer.messageBuilder().addData(message.getBytes()).build(), null);
        System.out.println(" [x] 'gcc --version' message sent");
    }
}

class FormData {
    List<CMD> commands;
    String image;
    String submit_id;

    public FormData(List<CMD> commands, String image, String submit_id) {
        this.commands = commands;
        this.image = image;
        this.submit_id = submit_id;
    }

    public FormData() {
    }

    public List<CMD> getCommands() {
        return commands;
    }

    public String getImage() {
        return image;
    }

    public String getSubmit_id() {
        return submit_id;
    }

    public void setCommands(List<CMD> commands) {
        this.commands = commands;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setSubmit_id(String submit_id) {
        this.submit_id = submit_id;
    }
}

class CMD {
    String command;
    List<String> args;
    String input;
    Config config;

    public CMD(List<String> args, String command, Config config, String input) {
        this.args = args;
        this.command = command;
        this.config = config;
        this.input = input;
    }

    public CMD() {
    }

    public String getCommand() {
        return command;
    }

    public List<String> getArgs() {
        return args;
    }

    public String getInput() {
        return input;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}

class Config {
    Integer time_limit;
    Integer time_reserved;
    Integer memory_limit;
    Integer memory_reserved;
    Boolean large_stack;
    Integer output_limit;
    Integer process_limit;

    public Config(Integer time_limit, Integer time_reserved, Integer memory_limit, Integer memory_reserved, Boolean large_stack, Integer output_limit, Integer process_limit) {
        this.time_limit = time_limit;
        this.time_reserved = time_reserved;
        this.memory_limit = memory_limit;
        this.memory_reserved = memory_reserved;
        this.large_stack = large_stack;
        this.output_limit = output_limit;
        this.process_limit = process_limit;
    }

    public Config() {
    }

    public Integer getTime_limit() {
        return time_limit;
    }

    public Integer getMemory_limit() {
        return memory_limit;
    }

    public Integer getTime_reserved() {
        return time_reserved;
    }

    public Integer getMemory_reserved() {
        return memory_reserved;
    }

    public Boolean getLarge_stack() {
        return large_stack;
    }

    public Integer getOutput_limit() {
        return output_limit;
    }

    public Integer getProcess_limit() {
        return process_limit;
    }

    public void setTime_limit(Integer time_limit) {
        this.time_limit = time_limit;
    }

    public void setMemory_limit(Integer memory_limit) {
        this.memory_limit = memory_limit;
    }

    public void setMemory_reserved(Integer memory_reserved) {
        this.memory_reserved = memory_reserved;
    }

    public void setTime_reserved(Integer time_reserved) {
        this.time_reserved = time_reserved;
    }

    public void setLarge_stack(Boolean large_stack) {
        this.large_stack = large_stack;
    }

    public void setProcess_limit(Integer process_limit) {
        this.process_limit = process_limit;
    }

    public void setOutput_limit(Integer output_limit) {
        this.output_limit = output_limit;
    }
}

enum ExitState {
    Success,
    RuntimeError,
    TimeLimitExceeded,
    MemoryLimitExceeded,
    OtherError
}

class SandboxResult {
    ExitState state;
    String stdout;
    String stderr;
}
