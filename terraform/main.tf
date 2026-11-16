
terraform {
  required_version = ">= 1.17.0"

  required_providers {
    docker = {
      source  = "kreuzwerker/docker"
      version = "4.6.0"
    }
  }
}

provider "docker" {
  host = "npipe:////.//pipe//dockerDesktopLinuxEngine"
}

resource "docker_image" "app" {
  name         = "cen4802-readiness:iac-a10"
  keep_locally = false

  build {
    context = abspath("${path.module}/..")
  }

  triggers = {
    jar_sha256 = filesha256("${path.module}/../target/cen4802-0.0.1-SNAPSHOT.jar")
  }
}

resource "docker_network" "app" {
  name   = "cen4802-iac-a10-net"
  driver = "bridge"
}

resource "docker_container" "app" {
  name    = "cen4802-iac-a10"
  image   = docker_image.app.image_id
  memory  = 768
  restart = "unless-stopped"

  networks_advanced {
    name = docker_network.app.name
  }

  ports {
    internal = 8080
    external = 8087
    ip       = "127.0.0.1"
  }
}

output "application_url" {
  value = "http://localhost:8087"
}
