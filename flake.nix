{
  description = "E-Library Project Flake";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs =
    {
      self,
      nixpkgs,
      flake-utils,
    }:
    flake-utils.lib.eachDefaultSystem (
      system:
      let
        pkgs = import nixpkgs {
          inherit system;
          config.allowUnfree = true; # Required for IntelliJ Ultimate
        };

        # --- Toolchain ---
        javaVersion = pkgs.jdk21;
        nodeVersion = pkgs.nodejs_20;
        mavenVersion = pkgs.maven;

        # --- IntelliJ Configuration ---
        # We use the standard Ultimate package.
        # Note: If your nixpkgs version insists on 'jetbrains.idea',
        # swap the line below to: myIntellij = pkgs.jetbrains.idea;
        myIntellij = pkgs.jetbrains.idea;

        # --- Backend Environment ---
        backendShell = pkgs.mkShell {
          name = "java-backend-env";
          buildInputs = [
            javaVersion
            mavenVersion
            pkgs.spring-boot-cli
          ];
          shellHook = ''
            export JAVA_HOME=${javaVersion}
            export PATH=$JAVA_HOME/bin:$PATH
            echo "☕ Java Backend Environment Loaded"
          '';
        };

        # --- Frontend Environment ---
        frontendShell = pkgs.mkShell {
          name = "react-frontend-env";
          buildInputs = [
            nodeVersion
            pkgs.yarn
          ];
          shellHook = ''
            echo "⚛️ React Frontend Environment Loaded"
          '';
        };

      in
      {
        devShells = {
          default = pkgs.mkShell {
            inputsFrom = [
              backendShell
              frontendShell
            ];

            buildInputs = [
              myIntellij
              # Optional: CLI tools for when you aren't using the IDE
              pkgs.docker
              pkgs.docker-compose
              pkgs.postgresql
              pkgs.steam-run
            ];

            shellHook = ''
              export JAVA_HOME=${javaVersion}
              export PATH=$JAVA_HOME/bin:$PATH

              export NIX_LD=$(cat "${pkgs.stdenv.cc}/nix-support/dynamic-linker")

                        export NIX_LD_LIBRARY_PATH="${
                          pkgs.lib.makeLibraryPath (
                            with pkgs;
                            [
                              stdenv.cc.cc.lib
                              zlib
                              openssl
                              glib
                              glibc
                            ]
                          )
                        }:$NIX_LD_LIBRARY_PATH"

              echo "Full Stack E-Library Environment Loaded"
              echo "----------------------------------------"
              echo "To launch IntelliJ:  idea-ultimate &"
              echo "(Lombok, Docker, & DB tools are bundled in this version)"
              echo "----------------------------------------"
            '';
          };

          backend = backendShell;
          frontend = frontendShell;
        };
      }
    );
}
