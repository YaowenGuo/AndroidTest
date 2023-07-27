plugins {
    id("cpp-library")
}

library {
    linkage.set(listOf(Linkage.SHARED))
    val javaHome = System.getProperty("java.home")
    binaries.configureEach {
        val compileTask = compileTask.get()
        compileTask.includes.from("$javaHome/include")

        val osFamily = targetPlatform.targetMachine.operatingSystemFamily
        if (osFamily.isMacOs) {
            compileTask.includes.from("$javaHome/include/darwin")
        } else if (osFamily.isLinux) {
            compileTask.includes.from("$javaHome/include/linux")
        } else if (osFamily.isWindows) {
            compileTask.includes.from("$javaHome/include/win32")
        }

        compileTask.source(
            fileTree(mapOf("dir" to "src/main/cpp", "include" to listOf("**/*.c", "**/*.cpp"))))

        if (toolChain is VisualCpp) {
            compileTask.compilerArgs.addAll("/TC")
        } else if (toolChain is GccCompatibleToolChain) {
            compileTask.compilerArgs.addAll("-x", "c", "-std=c11")
        }
    }
}