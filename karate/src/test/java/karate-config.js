function fn() {
  var env = karate.env; // get system property 'karate.env'
  karate.log('karate.env system property was:', env);
  if (!env) {
    env = 'pre';
  }
  var config = {
    env: env,
   
    //myVarName: 'someValue'
  }
  
  karate.set(read('classpath:config-'+env+'.yml'));
  
  if (env == 'mock') {
    // customize
    // e.g. config.foo = 'bar';
    
  } else if (env == 'des') {
    // customize
  }
  return config;
}