download(artifact(:jsyslog_message) => 'https://github.com/downloads/realityforge/jsyslog-message/jsyslog-message-1.0.jar')
download(artifact(:jsyslog_message_sources) => 'https://github.com/downloads/realityforge/jsyslog-message/jsyslog-message-1.0-sources.jar')

desc 'Tarrabah: A small library for receiving and transforming events into information'
define 'tarrabah' do
  project.version = `git describe --tags --always`.strip
  project.group = 'tarrabah'
  compile.options.source = '1.6'
  compile.options.target = '1.6'
  compile.options.lint = 'all'

  compile.with :guava,
               :javax_annotation,
               :javax_enterprise,
               :jsr305,
               :javax_el,
               :javax_inject,
               :javassist,
               :javax_interceptors,
               :cal10n_api,
               :slf4j_api,
               :slf4j_ext,
               :slf4j_jdk14,
               :weld_api,
               :weld_core,
               :weld_spi,
               :weld_se_core,
               :jsyslog_message

  package(:jar)
end

task 'exec' do
  cp = (Buildr.project('tarrabah').packages + Buildr.project('tarrabah').compile.dependencies)
  cp.each { |c| c.invoke }
  classpath = cp.collect { |c| c.to_s }.join(':')
  sh "java -Djava.util.logging.config.file=etc/logging.properties -cp #{classpath} org.jboss.weld.environment.se.StartMain testing"
end
