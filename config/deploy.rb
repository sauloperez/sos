# config valid only for Capistrano 3.1
lock '3.1.0'

set :application, 'sos'
set :repo_url, 'git@github.com:sauloperez/sos.git'

set :ssh_options, {
  forward_agent: true
}

ask :branch, proc { `git rev-parse --abbrev-ref HEAD`.chomp }

set :deploy_to, '/var/www/sos'

set :use_sudo, false

set :deploy_via, :copy
set :copy_strategy, :export

set :linked_files, %w{config/redch.properties}

# Required when not using rvm or rbenv
SSHKit.config.command_map[:rake]  = "bundle exec rake"

after 'deploy:publishing', 'tomcat:compile'
after 'tomcat:compile', 'tomcat:deploy'
after 'tomcat:deploy', 'tomcat:restart'
