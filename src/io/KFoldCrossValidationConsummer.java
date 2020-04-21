package io;

import java.io.IOException;
import java.nio.file.Path;

public interface KFoldCrossValidationConsummer {

	public void consumFold(Path training, Path test, FoldInfo info) throws IOException;
}
