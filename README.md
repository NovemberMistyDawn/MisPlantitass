# MisPlantitass


Se trata de una aplicación sencilla, cuyo objetivo es brindar a los usuarios la posibilidad de, por un lado
registrar las necesidades de cuidado de sus plantas, como la fecha del último regado, y por otro lado, mediante
el acceso a la API de perenual.com, consultar dentro un listado de hasta 3.000 plantas, las necesidades específicas
de cuidado de las plantas que le sean de interés. Hay que tener en cuenta las limitaciones de nuestro plan, que 
es el gratuito, por lo que los datos de algunas plantas no serán accesibles.

IMPORTANTE: Cuando cargamos la pantalla de búsqueda en el botón "Búsqueda de plantas", hay que esperar unos 5 segundos 
para que nos aparezcan las plantas, debido a la gran cantidad de datos que necesita. Para realizar la búsqueda de plantas,
ha de escribirse o bien el nombre científico (el que está en latín) o en inglés. 


-La aplicación cuenta con 6 activities.

-Desde MyPlantsActivity, se envía un parámetro (extra) llamado PLANT_ID con el id de la planta, y en MyPlantDetailActivity,
se recupera ese extra y se utiliza para buscar la planta en base de datos.

-Cuando el usuario guarda una planta en la sección "Mis plantas", aparece un dialogo mediante Toast, que confirma
que la planta ha sido guardada.

-En la pantalla de bienvenida, se guarda el nombre del usuario mediante SharedPreferences.

-También incluye una tabla creada mediante SQLite, para guardar nuestras plantas y poder registrar sus cuidados.

-Accedemos a el API Rest de perenual y parseamos sus datos usando Gson y Retrofit.

-Además, hacemos uso de dos Adapters en la aplicación, uno para la parte del diccionario de plantas, y otro para 
el listado en el que registramos nuestras plantas.
