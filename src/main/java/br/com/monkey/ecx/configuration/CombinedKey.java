package br.com.monkey.ecx.configuration;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "of")
public class CombinedKey {

	String key;

}
