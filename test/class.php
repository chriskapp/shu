<?php

namespace Foo\Bar;

use Blub\Bla;
use Blub\Foo as Boo;

class Test extends Foo implements Bar
{
	public function __construct(Foo $foo, Bar\Foo $foo, \Bar\Foo $bar)
	{
		self::test();
		Foo::test();
		Bar\Foo::test();
		\Bar\Foo::test();
		Bla::test();
		Boo::test();
		
		new self();
		new Foo();
		new Bar\Foo();
		new \Bar\Foo();
		new Bla();
		new Boo();
		
		try
		{
		}
		catch(Foo $e)
		{
		}
		catch(Bar\Foo $e)
		{
		}
		catch(\Bar\Foo $e)
		{
		}
		catch(Bla $e)
		{
		}
		catch(Boo $e)
		{
		}
		
		if($x instanceof Foo)
		{
		}
		if($x instanceof Bar\Foo)
		{
		}
		if($x instanceof \Bar\Foo)
		{
		}
		if($x instanceof Bla)
		{
		}
		if($x instanceof Boo)
		{
		}
	}
}

class Bar extends \Bar\Foo implements Foo, Bar\Foo, \Bar\Foo, Bla, Boo
{
}

class Foo extends Bar\Foo
{
}

interface Foo
{
}

final class Target
{
}
