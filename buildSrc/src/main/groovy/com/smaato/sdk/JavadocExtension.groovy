package com.smaato.sdk

class JavadocExtension {

    private final List<String> sources = new ArrayList<>()

    List<String> getSources() {
        return Collections.unmodifiableList(sources)
    }

    void include(String... sources) {
        Collections.addAll(this.sources, sources)
    }

}