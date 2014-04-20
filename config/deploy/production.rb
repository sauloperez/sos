set :stage, :production

set :user, 'ubunt'
set :group, 'ubuntu'

set :ssh_options, {
  keys: %w(/Users/pau/.ssh/amazon.cer),
  forward_agent: true
}

server '54.72.170.113', user: 'ubuntu', roles: %w{sos}
