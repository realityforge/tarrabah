require 'buildr/git_auto_version'

download(artifact(:jsyslog_message) => 'http://cloud.github.com/downloads/realityforge/jsyslog-message/jsyslog-message-1.1.jar')
download(artifact(:jsyslog_message_sources) => 'http://cloud.github.com/downloads/realityforge/jsyslog-message/jsyslog-message-1.1-sources.jar')

desc 'Tarrabah: A small library for receiving and transforming events into information'
define 'tarrabah' do
  project.group = 'org.realityforge.tarrabah'
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
               :jsyslog_message,
               :netty,
               :joda_time,
               :gson,
               :deltaspike_cdictrl_api,
               :deltaspike_cdictrl_impl,
               :deltaspike_core_api,
               :deltaspike_core_impl

  test.using :testng

  # Ugly hack to ensure the beans.xml is included in same path as implementing classes
  test.compile do
    cp_r Dir["#{_(:target, :main, :resources)}/*"], _(:target, :main, :classes)
  end

  package(:jar)
end
