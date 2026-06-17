package br.com.monkey.ecx.configuration;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "of")
public class Alias {

	String alias;

	String key;

	List<CombinedKey> combinedKey;

}
