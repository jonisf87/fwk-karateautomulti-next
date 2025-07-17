function fn() {
  var env = karate.env;
  karate.log('karate.env system property was:', env);
  if (!env) {
    env = 'pre';
  }
  var config = { env: env };

  // Cargar configuración YAML según el entorno
  var configFile = 'classpath:config-' + env + '.yml';
  karate.log('Cargando configuración:', configFile);
  var loadedConfig = karate.read(configFile);
  karate.log('Configuración cargada:', loadedConfig);
  karate.configure('headers', { 'Content-Type': 'application/json' });
  karate.set(loadedConfig);

  return config;
}