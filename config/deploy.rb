# config valid only for Capistrano 3.1
lock '3.1.0'

set :application, 'sos'
set :repo_url, 'git@github.com:sauloperez/sos.git'

set :ssh_options, {
  forward_agent: true
}

ask :branch, proc { `git rev-parse --abbrev-ref HEAD`.chomp }

set :deploy_to, '/var/sos/'

set :use_sudo, false

set :deploy_via, :copy
set :copy_strategy, :export

# Default value for :log_level is :debug
# set :log_level, :debug

# Default value for :pty is false
# set :pty, true

# Default value for :linked_files is []
# set :linked_files, %w{config/database.yml}

# Default value for linked_dirs is []
# set :linked_dirs, %w{bin log tmp/pids tmp/cache tmp/sockets vendor/bundle public/system}

# Default value for default_env is {}
# set :default_env, { path: "/opt/ruby/bin:$PATH" }

namespace :deploy do

  desc 'Restart application'
  task :restart do
    on roles(:sos), in: :sequence, wait: 5 do
      sudo '/etc/init.d/tomcat7 restart'
    end
  end

  after :finishing, :restart
end
