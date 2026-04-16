function fn() {
  var env = karate.env || 'pre';
  var environmentConfig = karate.read('classpath:config-' + env + '.yml');
  var config = {
    env: env,
    urls: environmentConfig.urls || {},
    timeouts: environmentConfig.timeouts || {
      connect: 5000,
      read: 10000
    }
  };

  karate.configure('connectTimeout', config.timeouts.connect);
  karate.configure('readTimeout', config.timeouts.read);

  return config;
}
