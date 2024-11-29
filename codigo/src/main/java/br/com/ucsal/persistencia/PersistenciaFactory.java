package br.com.ucsal.persistencia;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import br.com.ucsal.annotation.Inject;
import br.com.ucsal.annotation.utils.DBType;

public class PersistenciaFactory {

	private static final Map<DBType, ProdutoRepository<?, ?>> commands = new HashMap<>();

	static {
		commands.put(DBType.HSQLDB, new HSQLProdutoRepository());
		commands.put(DBType.MEMORIA, MemoriaProdutoRepository.getInstancia());
	}

	public static <T> T injectDependencies(T instance) {
		try {
			Field[] fields = instance.getClass().getDeclaredFields();

			for (Field field : fields) {

				if (field.isAnnotationPresent(Inject.class)) {
					Inject inject = field.getAnnotation(Inject.class);
					DBType dbType = inject.value();

					ProdutoRepository<?, ?> repository = commands.get(dbType);
					if (repository == null) {
						throw new IllegalStateException("Nenhuma implementação encontrada para " + dbType);
					}

					field.setAccessible(true);
					field.set(instance, repository);

				}
			}

			return instance;

		} catch (Exception e) {
			throw new RuntimeException("Erro ao injetar dependências para " + instance.getClass().getName(), e);
		}
	}
}
