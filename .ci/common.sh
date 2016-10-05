WORKDIR=$PWD

RED="\e[31m"
GREEN="\e[32m"
NORMAL="\e[0m"

function error() {
  echo -e "\n$RED-------> $1 $NORMAL"
}

function info() {
  echo -e "\n$GREEN-------> $1 $NORMAL"
}

show_spinner()
{
  local -r pid="${1}"
  local -r delay='1m'
  local spinstr='\|/-'
  local temp
  while ps a | awk '{print $1}' | grep -q "${pid}"; do
    echo "Still running... ¯\_(ツ)_/¯"
    sleep "${delay}"
  done
}


